MedVault Android app (modified)

What this contains
- Android app (Kotlin) with three main tabs wired to backend services:
  - Home: Doctor prediction model (POST /predict on model service)
  - Dashboard: Upload PNG/JPG medical records and call summary service (POST /summarize/)
  - Notifications: Upload prescription images and call extract_medicines/ to extract meds and schedule alarms

Important notes
- The app uses two separate backend services:
  - Model service (FastAPI) — default expected at http://10.0.2.2:8000/ (see `Model/main.py`)
  - Summary service (FastAPI) — default expected at http://10.0.2.2:8001/ (see `summary_service/main.py`)

- Android emulator: use 10.0.2.2 to reach host machine localhost. If running on a device, replace with the machine's LAN IP or expose services.

Configuration
- Create `local.properties` at the project root (a sample is provided at `local.properties.sample`).
- Set `DEV_HOST_IP` to the IP of the machine running the backend services (reachable from your phone). Example:

  DEV_HOST_IP=10.100.27.122

- The Gradle build will read this property and inject BuildConfig fields `MODEL_BASE` and `SUMMARY_BASE` automatically. Rebuild the app after changing `local.properties`.

Permissions
- INTERNET is declared in `AndroidManifest.xml`.
- For Android 13+, `READ_MEDIA_IMAGES` is declared. For older Android versions the app declares `READ_EXTERNAL_STORAGE`.
- The app does not yet request runtime permissions — add runtime permission handling for production.

How to run the backend services (dev)
1. Model service (FastAPI)
   - cd Model
   - Create venv, install requirements from `requirements.txt`
   - Run: `uvicorn main:app --reload --port 8000`

2. Summary service
   - cd summary_service
   - Create venv, install requirements from `requirements.txt`
   - Set GEMINI_API_KEY in env (if using Gemini) or mock
   - Run: `uvicorn main:app --reload --port 8001`

Build and run the Android app
- Open Android Studio and import the `MedVault-MobileApp` gradle project.
- Build and run on emulator.

Next suggested improvements (I can implement them):
- Runtime permission handling for image read access.
- Better error UI and loading states.
- Persist scheduled alarms and allow custom times per medication.
- Integrate with the main backend (Medvault) authentication flow.

If you want, I can now:
- Add runtime permission requests and checks.
- Change the app to read base URLs from `local.properties` or a config file.
- Implement persistent alarm scheduling and allow per-med scheduling UI.

Tell me which of these you'd like next and I'll implement it.