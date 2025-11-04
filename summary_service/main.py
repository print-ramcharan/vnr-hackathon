from fastapi import FastAPI, UploadFile, File
from fastapi.staticfiles import StaticFiles
import google.generativeai as genai
from dotenv import load_dotenv
import os
import logging
import json
from fastapi.middleware.cors import CORSMiddleware


# 🧾 Setup logging
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(message)s",
    handlers=[logging.StreamHandler()]
)

# 🔑 Load environment variables
load_dotenv()
api_key = os.getenv("GEMINI_API_KEY")

if not api_key:
    logging.error("❌ GEMINI_API_KEY not found in environment variables!")
else:
    logging.info("✅ GEMINI_API_KEY loaded successfully.")

# 🚀 Initialize FastAPI and Gemini
app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # or ["http://127.0.0.1:5500"] if using Live Server
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)
genai.configure(api_key=api_key)


model = genai.GenerativeModel("gemini-2.0-flash")

# =============================
# 🧾 NEW: Prescription Extractor
# =============================
@app.post("/extract_medicines/")
async def extract_medicines(file: UploadFile = File(...)):
    logging.info(f"📂 Received prescription file: {file.filename} ({file.content_type})")

    try:
        content = await file.read()
        logging.info(f"📄 File size: {len(content)} bytes")

        model = genai.GenerativeModel("gemini-2.0-flash")

        if file.content_type and file.content_type.startswith("image/"):
            logging.info("🧠 Extracting medicine data from prescription image...")
            prompt = (
        "You are an expert pharmacy assistant. Carefully read the uploaded prescription image "
        "and extract ONLY the prescribed medicines in a consistent JSON-like Markdown format.\n\n"
        "For each medicine, include the following fields exactly as shown:\n"
        "- Medicine Name: <name>\n"
        "- Dosage: <dosage or 'N/A'>\n"
        "- Frequency: <frequency or 'N/A'>\n"
        "- Route of Administration: <route or 'N/A'>\n"
        "- Duration: <duration or 'N/A'>\n\n"
        "Use one bullet point (*) per medicine. Example format:\n"
        "* Medicine Name: Paracetamol\n"
        "  Dosage: 500 mg\n"
        "  Frequency: Twice daily\n"
        "  Route of Administration: Oral\n"
        "  Duration: 5 days\n\n"
        "Ensure the format remains exactly the same for all entries — no extra commentary, no summaries, "
        "no section titles, and no deviations from this key format."
    )


            response = model.generate_content([
                prompt,
                {"mime_type": file.content_type, "data": content}
            ])

            summary = getattr(response, "text", "⚠️ No medicine data found.")
            logging.info("✅ Received structured medicine data from Gemini.")
            logging.info(json.dumps(response.to_dict(), indent=2, ensure_ascii=False))

            return {"summary": summary, "raw": response.to_dict()}

        else:
            return {"error": "Please upload an image file (jpg/png)"}

    except Exception as e:
        logging.exception("❌ Error while processing prescription:")
        return {"error": str(e)}


@app.post("/summarize/")
async def summarize_patient_file(file: UploadFile = File(...)):
    """
    Handles uploaded files (text, PNG, JPG, JPEG, etc.) and summarizes their content
    using Google's Gemini API.
    """
    logging.info(f"📂 Received file: {file.filename} ({file.content_type})")

    try:
        # Read file contents
        content = await file.read()
        logging.info(f"📄 File size: {len(content)} bytes")

        model = genai.GenerativeModel("gemini-2.0-flash")  # ✅ stable model

        # 🖼️ If image file
        if file.content_type and file.content_type.startswith("image/"):
            logging.info("🧠 Sending image to Gemini for summarization...")
            prompt = (
                "You are a medical assistant. Analyze this medical image "
                "and summarize key patient details, prescriptions, or visible notes clearly for a doctor."
            )

            # Send binary image directly
            response = model.generate_content(
                [prompt, {"mime_type": file.content_type, "data": content}]
            )

            logging.info("✅ Received image summary from Gemini.")
            logging.info("🧩 Full response object:")
            logging.info(json.dumps(response.to_dict(), indent=2, ensure_ascii=False))  # 👀 Print full details

            summary = getattr(response, "text", "⚠️ No summary returned.")
            logging.info(f"📝 Summary: {summary}")

            return {
                "summary": summary,
                "full_response": response.to_dict()  # 👀 return full Gemini output
            }

        # 🧾 If text file
        else:
            logging.info("🧾 Processing text document...")

            try:
                text = content.decode("utf-8", errors="ignore")
                logging.info("✅ Successfully decoded text file.")
            except Exception as e:
                logging.error(f"❌ Error decoding text: {e}")
                text = ""

            if not text.strip():
                logging.warning("⚠️ File has no readable text content.")

            prompt = (
                "You are a medical assistant. Summarize the following patient information clearly and concisely for a doctor:\n"
                f"{text}"
            )

            logging.info("🧠 Sending text to Gemini for summarization...")
            response = model.generate_content(prompt)
            logging.info("✅ Received text summary from Gemini.")
            logging.info("🧩 Full response object:")
            logging.info(json.dumps(response.to_dict(), indent=2, ensure_ascii=False))

            summary = getattr(response, "text", "⚠️ No summary returned.")
            logging.info(f"📝 Summary: {summary}")

            return {
                "summary": summary,
                "full_response": response.to_dict()
            }

    except Exception as e:
        logging.exception("❌ Error while processing file:")
        return {"error": str(e)}



def extract_text(response):
    if hasattr(response, "text") and response.text:
        return response.text
    try:
        return response.candidates[0].content.parts[0].text
    except Exception:
        return "⚠️ No summary returned."

# =============================
# 🗂️ Multiple File Summarizer
# =============================
@app.post("/summarize_all/")
async def summarize_all_files(files: list[UploadFile] = File(...)):
    """
    Handles multiple uploaded files and generates a single short summary paragraph.
    """
    logging.info(f"📦 Received {len(files)} files for summarization.")
    summaries = []

    try:
        for idx, file in enumerate(files):
            logging.info(f"📁 Processing file {idx + 1}/{len(files)}: {file.filename} ({file.content_type})")

            content = await file.read()
            if not content:
                logging.warning(f"⚠️ File {file.filename} is empty, skipping.")
                continue

            try:
                if file.content_type and file.content_type.startswith("image/"):
                    logging.info(f"🖼️ File {file.filename} detected as image.")
                    prompt = (
                        "Extract only key points (diagnosis, prescription, test results, or advice) "
                        "from this medical image without detailed formatting."
                    )

                    response = model.generate_content(
                        [prompt, {"mime_type": file.content_type, "data": content}]
                    )
                    logging.info(f"✅ Received Gemini response for image {file.filename}.")
                    logging.debug(json.dumps(response.to_dict(), indent=2, ensure_ascii=False))

                    summary = extract_text(response)
                    logging.info(f"📝 Image summary: {summary[:150]}...")

                else:
                    logging.info(f"📄 File {file.filename} detected as text/document.")
                    text = content.decode("utf-8", errors="ignore")
                    if not text.strip():
                        logging.warning(f"⚠️ File {file.filename} has no readable text content.")

                    prompt = (
                        "Summarize this medical document briefly, only mentioning "
                        "important medical details in one paragraph:\n"
                        f"{text}"
                    )

                    response = model.generate_content(prompt)
                    logging.info(f"✅ Received Gemini response for text {file.filename}.")
                    logging.debug(json.dumps(response.to_dict(), indent=2, ensure_ascii=False))

                    summary = extract_text(response)
                    logging.info(f"📝 Text summary: {summary[:150]}...")

                summaries.append(summary.strip())

            except Exception as inner_e:
                logging.exception(f"❌ Error while summarizing file {file.filename}: {inner_e}")

        if not summaries:
            logging.warning("⚠️ No summaries were generated for any files.")
            return {"error": "No summaries could be generated from provided files."}

        logging.info(f"🧠 Combining {len(summaries)} individual summaries...")
        combined_prompt = (
            "You are a concise medical assistant. Merge the following short summaries "
            "into a single coherent paragraph (no lists, no repetition, under 100 words):\n\n"
            + "\n".join(summaries)
        )

        combined_response = model.generate_content(combined_prompt)
        logging.info("✅ Received combined Gemini summary.")
        logging.debug(json.dumps(combined_response.to_dict(), indent=2, ensure_ascii=False))

        combined_summary = extract_text(combined_response)
        logging.info(f"🧾 Final combined summary: {combined_summary}")

        return {
            "summary": combined_summary,
            "all_summaries": summaries
        }

    except Exception as e:
        logging.exception("❌ Error while summarizing multiple files:")
        return {"error": str(e)}

# ✅ Serve static frontend files (index.html)
app.mount("/", StaticFiles(directory=".", html=True), name="static")
