import React from 'react';
import { useNavigate } from 'react-router-dom';
import { EnhancedButton } from '@/components/ui/enhanced-button';
import { Shield, Heart, Users, Activity, CheckCircle, Star, Stethoscope, FileText, Clock, Award } from 'lucide-react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import MedVaultChatbot from '@/components/MedVaultChatbot';
import docBg from '@/assets/bg-green.jpg'; 
import threedoc from '@/assets/doctors.jpg'; 
import TextType from '@/components/ui/TypeText';

const Landing = () => {
  const navigate = useNavigate();

  const features = [
    {
      icon: FileText,
      title: "Digital Records",
      description: "Comprehensive electronic health records accessible anytime, anywhere with advanced search capabilities."
    },
    {
      icon: Clock,
      title: "Smart Scheduling",
      description: "Intelligent appointment booking system with automated reminders and calendar integration."
    },
    {
      icon: Shield,
      title: "Privacy First",
      description: "End-to-end encryption and HIPAA compliance ensuring maximum security for sensitive data."
    },
    {
      icon: Award,
      title: "Quality Care",
      description: "Evidence-based insights and analytics to improve patient outcomes and care delivery."
    }
  ];

  const stats = [
    { number: "50K+", label: "Active Patients" },
    { number: "1,200+", label: "Healthcare Providers" },
    { number: "99.9%", label: "Uptime Guarantee" },
    { number: "24/7", label: "Support Available" }
  ];

  const benefits = [
    "Centralized medical record management",
    "Seamless appointment scheduling",
    "Real-time health monitoring",
    "Secure doctor-patient communication",
    "Advanced analytics and reporting",
    "HIPAA compliant infrastructure"
  ];

  const faqs = [
    {
      question: "Is MedVault HIPAA compliant?",
      answer: "Yes, MedVault is fully HIPAA compliant with end-to-end encryption and secure data storage."
    },
    {
      question: "How secure is patient data?",
      answer: "We use military-grade encryption and follow strict security protocols to ensure patient data is always protected."
    },
    {
      question: "Can I integrate with existing systems?",
      answer: "MedVault offers seamless integration with most EHR systems and healthcare platforms."
    },
    {
      question: "What support do you offer?",
      answer: "24/7 dedicated support team with healthcare IT specialists to assist with any issues."
    }
  ];

  const testimonials = [
    {
      name: "Dr. Sarah Chen",
      role: "Chief Medical Officer",
      content: "MedVault transformed our practice. Patient records are now instantly accessible and secure.",
      avatar: "SC"
    },
    {
      name: "Dr. Michael Rodriguez",
      role: "Family Physician",
      content: "The scheduling system alone saved us 10+ hours per week. Incredible platform!",
      avatar: "MR"
    },
    {
      name: "Healthcare Systems Inc.",
      role: "Medical Group",
      content: "Implementation was smooth and our staff adapted quickly. Highly recommended!",
      avatar: "HS"
    }
  ];
  const smoothScroll = (id: string) => {
  const element = document.getElementById(id);
  if (element) {
    element.scrollIntoView({
      behavior: 'smooth',
      block: 'start'
    });
  }
};
  return (
    <div className="min-h-screen bg-background">
      
      {/* Header */}
      <header className="sticky top-0 z-50 bg-background/95 backdrop-blur-md border-b border-border">
        <div className="container mx-auto px-4 py-4">
          <nav className="flex items-center justify-between">
            <div className="flex items-center space-x-3">
              <div className="relative">
                <div className="w-10 h-10 bg-gradient-medical rounded-xl flex items-center justify-center shadow-lg">
                  <Stethoscope className="w-6 h-6 text-primary-foreground" />
                </div>
                <div className="absolute -top-1 -right-1 w-4 h-4 bg-primary-dark rounded-full border-2 border-background"></div>
              </div>
              <div>
                <h1 className="text-2xl font-bold text-primary-dark">MedVault</h1>
                <p className="text-xs text-muted-foreground">Healthcare Platform</p>
              </div>
            </div>
            
           <div className="hidden md:flex items-center space-x-8">
            <a href="#home" 
             onClick={(e) => {
                e.preventDefault();
                smoothScroll('home');
              }}
            className="text-foreground/80 hover:text-primary transition-colors font-medium relative group">
              Home
              <span className="absolute bottom-0 left-0 w-0 h-0.5 bg-primary-dark group-hover:w-full transition-all duration-300"></span>
            </a>
            <a href="#about" 
            onClick={(e) => {
              e.preventDefault();
              smoothScroll('about');
            }}
            className="text-foreground/80 hover:text-primary transition-colors font-medium relative group">
              About
              <span className="absolute bottom-0 left-0 w-0 h-0.5 bg-primary-dark group-hover:w-full transition-all duration-300"></span>
            </a>
            <a href="#faq" 
                onClick={(e) => {
                  e.preventDefault();
                  smoothScroll('faq');
                }}
            className="text-foreground/80 hover:text-primary transition-colors font-medium relative group">
              FAQ
              <span className="absolute bottom-0 left-0 w-0 h-0.5 bg-primary-dark group-hover:w-full transition-all duration-300"></span>
            </a>
            <a href="#testimonials"
              onClick={(e) => {
                e.preventDefault();
                smoothScroll('testimonials');
            }}
            className="text-foreground/80 hover:text-primary transition-colors font-medium relative group">
              Testimonials
              <span className="absolute bottom-0 left-0 w-0 h-0.5 bg-primary-dark group-hover:w-full transition-all duration-300"></span>
            </a>
            <a href="#contact" 
            onClick={(e) => {
              e.preventDefault();
              smoothScroll('contact');
            }}
            className="text-foreground/80 hover:text-primary transition-colors font-medium relative group">
              Contact
              <span className="absolute bottom-0 left-0 w-0 h-0.5 bg-primary-dark group-hover:w-full transition-all duration-300"></span>
            </a>
          </div>
            
            <div className="flex items-center space-x-3">
              <EnhancedButton 
                variant="ghost" 
                onClick={() => navigate('/login')}
                className="text-foreground/80 hover:text-primary hover:bg-primary/5"
              >
                Sign In
              </EnhancedButton>
              <EnhancedButton 
                variant="medical" 
                onClick={() => navigate('/login')}
                className="shadow-md hover:shadow-lg bg-primary hover:bg-primary-dark text-primary-foreground"
              >
                Get Started
              </EnhancedButton>
            </div>
          </nav>
        </div>
      </header>

      {/* Hero Section with Doctor Background */}
      <section id="home" className="relative min-h-screen flex items-center justify-center overflow-hidden">
        {/* Doctor Background Image */}
        <div className="absolute inset-0">
          <img src={docBg} alt="Doctor background" className="w-full h-full object-cover" />
          <div className="absolute inset-0 bg-primary-darker/70 backdrop-blur-[1px]"></div>
        </div>
        
        <div className="container mx-auto px-4 py-20 relative z-10">
          <div className="grid lg:grid-cols-2 gap-12 items-center">
            <div className="space-y-8 animate-fade-in">
              <div className="space-y-4">
                <div className="inline-flex items-center px-4 py-2 bg-primary/20 backdrop-blur-sm rounded-full text-sm text-primary-dark font-medium border border-primary/30">
                  <Star className="w-4 h-4 mr-2 text-primary" />
                  Trusted by 50,000+ Healthcare Professionals
                </div>
                <h1 className="text-4xl md:text-6xl font-bold leading-tight text-foreground">
                  
                  <TextType 
                  text={
                    "Modern Healthcare"}
                  typingSpeed={70}
                  pauseDuration={1500}
                  showCursor={true}
                  cursorCharacter=""
                  textColors={["text-foreground"]}
                />
                  <span className="text-primary block">Management Platform</span>
                </h1>
                
                <p className="text-xl text-foreground/90 leading-relaxed max-w-lg">
                  Streamline your medical practice with secure record management, 
                  intelligent scheduling, and powerful analytics—all in one platform.
                </p>
              </div>
              
              <div className="flex flex-col sm:flex-row gap-4">
                <EnhancedButton 
                  variant="medical" 
                  size="xl"
                  onClick={() => navigate('/login')}
                  className="shadow-elegant hover:shadow-xl transform hover:scale-105 transition-all bg-primary text-primary-foreground hover:bg-primary-dark border-0"
                >
                  Start Free Trial
                </EnhancedButton>
                <EnhancedButton 
                  variant="hero" 
                  size="xl"
                  onClick={() => navigate('/login')}
                  className="group bg-primary/10 border-2 border-primary text-primary hover:bg-primary hover:text-primary-foreground"
                >
                  <Activity className="w-5 h-5 mr-2 group-hover:animate-pulse" />
                  Healthcare Portal
                </EnhancedButton>
              </div>

              {/* Stats */}
              <div className="grid grid-cols-2 md:grid-cols-4 gap-6 pt-8">
                {stats.map((stat, index) => (
                  <div key={index} className="text-center">
                    <div className="text-2xl md:text-3xl font-bold text-primary">{stat.number}</div>
                    <div className="text-sm text-foreground/80">{stat.label}</div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* About Section */}
      <section id="about" className="relative py-20 bg-background">
        <div className="container mx-auto px-4">
          <div className="grid lg:grid-cols-2 gap-12 items-center">
            {/* Left Side - Image */}
            <div className="relative">
              <div className="relative rounded-2xl overflow-hidden shadow-elegant border border-border">
                <img src={threedoc} alt="MedVault Healthcare Platform" className="w-full h-auto object-cover" />
                <div className="absolute inset-0 bg-gradient-to-t from-primary/20 to-transparent"></div>
              </div>
              {/* Floating Stats */}
              <div className="absolute -bottom-6 -right-6 bg-background rounded-2xl p-6 shadow-elegant border border-border">
                <div className="text-center">
                  <div className="text-2xl font-bold text-primary">98%</div>
                  <div className="text-sm text-muted-foreground">Satisfaction Rate</div>
                </div>
              </div>
            </div>

            {/* Right Side - About Content */}
            <div className="space-y-6">
              <h2 className="text-3xl md:text-4xl font-bold text-foreground">
                Revolutionizing Healthcare Management
              </h2>
              <p className="text-lg text-muted-foreground leading-relaxed">
                MedVault is built by healthcare professionals for healthcare professionals. 
                Our platform combines cutting-edge technology with deep industry expertise 
                to deliver the most comprehensive healthcare management solution available.
              </p>
              
              {/* Healthcare Icons Grid */}
              <div className="grid grid-cols-2 gap-6 pt-6">
                <div className="flex items-center space-x-3 p-4 rounded-xl bg-primary-pastel border border-primary/20">
                  <div className="w-12 h-12 bg-primary/15 rounded-xl flex items-center justify-center">
                    <Shield className="w-6 h-6 text-primary" />
                  </div>
                  <div>
                    <div className="font-semibold text-foreground">Secure</div>
                    <div className="text-sm text-muted-foreground">HIPAA Compliant</div>
                  </div>
                </div>
                <div className="flex items-center space-x-3 p-4 rounded-xl bg-primary-pastel border border-primary/20">
                  <div className="w-12 h-12 bg-primary/15 rounded-xl flex items-center justify-center">
                    <Activity className="w-6 h-6 text-primary" />
                  </div>
                  <div>
                    <div className="font-semibold text-foreground">Efficient</div>
                    <div className="text-sm text-muted-foreground">Time Saving</div>
                  </div>
                </div>
                <div className="flex items-center space-x-3 p-4 rounded-xl bg-primary-pastel border border-primary/20">
                  <div className="w-12 h-12 bg-primary/15 rounded-xl flex items-center justify-center">
                    <Users className="w-6 h-6 text-primary" />
                  </div>
                  <div>
                    <div className="font-semibold text-foreground">Collaborative</div>
                    <div className="text-sm text-muted-foreground">Team Friendly</div>
                  </div>
                </div>
                <div className="flex items-center space-x-3 p-4 rounded-xl bg-primary-pastel border border-primary/20">
                  <div className="w-12 h-12 bg-primary/15 rounded-xl flex items-center justify-center">
                    <Award className="w-6 h-6 text-primary" />
                  </div>
                  <div>
                    <div className="font-semibold text-foreground">Quality</div>
                    <div className="text-sm text-muted-foreground">Award Winning</div>
                  </div>
                </div>
              </div>

              <EnhancedButton 
                variant="medical" 
                onClick={() => navigate('/login')}
                className="mt-6 bg-primary hover:bg-primary-dark text-primary-foreground"
              >
                Learn More About Us
              </EnhancedButton>
            </div>
          </div>
        </div>
      </section>

      {/* FAQ Section */}
      <section id="faq" className="relative py-20 bg-primary-pastel overflow-hidden">
        

        {/* Content Layer */}
        <div className="relative z-10">
          <div className="container mx-auto px-4">
            <div className="text-center mb-16">
              <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-4">
                Frequently Asked Questions
              </h2>
              <p className="text-xl text-muted-foreground max-w-2xl mx-auto">
                Get answers to the most common questions about MedVault platform.
              </p>
            </div>

            <div className="max-w-4xl mx-auto space-y-6">
              {faqs.map((faq, index) => (
                <Card key={index} className="bg-background/95 backdrop-blur-md hover:shadow-card transition-all duration-300 border-border hover-lift">
                  <CardContent className="p-6">
                    <h3 className="text-lg font-semibold mb-2 text-primary-dark">
                      {faq.question}
                    </h3>
                    <p className="text-muted-foreground">{faq.answer}</p>
                  </CardContent>
                </Card>
              ))}
            </div>
          </div>
        </div>
      </section>

      {/* Testimonials Section */}
      <section id="testimonials" className="py-20 bg-background">
        <div className="container mx-auto px-4">
          <div className="text-center mb-16">
            <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-4">
              What Our Users Say
            </h2>
            <p className="text-xl text-muted-foreground max-w-2xl mx-auto">
              Join thousands of healthcare professionals who trust MedVault.
            </p>
          </div>

          <div className="grid md:grid-cols-3 gap-8">
            {testimonials.map((testimonial, index) => (
              <Card key={index} className="text-center hover:shadow-card transition-all duration-300 border-border hover-lift">
                <CardContent className="p-6">
                  <div className="w-16 h-16 bg-gradient-medical rounded-full mx-auto mb-4 flex items-center justify-center text-primary-foreground font-bold shadow-medical">
                    {testimonial.avatar}
                  </div>
                  <div className="flex justify-center mb-2">
                    {[...Array(5)].map((_, i) => (
                      <Star key={i} className="w-4 h-4 text-primary fill-current opacity-80" />
                    ))}
                  </div>
                  <p className="text-foreground/80 mb-4 italic">"{testimonial.content}"</p>
                  <div>
                    <div className="font-semibold text-foreground">{testimonial.name}</div>
                    <div className="text-sm text-muted-foreground">{testimonial.role}</div>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section className="py-20 bg-primary-pastel">
        <div className="container mx-auto px-4">
          <div className="text-center mb-16">
            <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-4">
              Why Choose MedVault?
            </h2>
            <p className="text-xl text-muted-foreground max-w-2xl mx-auto">
              Built with healthcare professionals in mind, offering cutting-edge features 
              for modern medical practice management.
            </p>
          </div>

          <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-8 mb-16">
            {features.map((feature, index) => (
              <Card key={index} className="text-center hover:shadow-card transition-all duration-300 border-border bg-background hover-lift">
                <CardHeader>
                  <div className="w-16 h-16 bg-gradient-medical rounded-full mx-auto mb-4 flex items-center justify-center shadow-medical">
                    <feature.icon className="w-8 h-8 text-primary-foreground" />
                  </div>
                  <CardTitle className="text-xl text-foreground">{feature.title}</CardTitle>
                </CardHeader>
                <CardContent>
                  <p className="text-muted-foreground">{feature.description}</p>
                </CardContent>
              </Card>
            ))}
          </div>

          {/* Benefits List */}
          <div className="bg-background rounded-2xl p-8 shadow-card border border-border">
            <h3 className="text-2xl font-bold text-center mb-8 text-foreground">
              Comprehensive Healthcare Management
            </h3>
            <div className="grid md:grid-cols-2 gap-4">
              {benefits.map((benefit, index) => (
                <div key={index} className="flex items-center space-x-3 p-3 rounded-lg bg-primary-pastel">
                  <CheckCircle className="w-5 h-5 text-primary flex-shrink-0" />
                  <span className="text-foreground font-medium">{benefit}</span>
                </div>
              ))}
            </div>
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-20 bg-gradient-medical">
        <div className="container mx-auto px-4">
          <div className="text-center rounded-2xl p-12 shadow-elegant bg-primary/95 backdrop-blur-sm">
            <h2 className="text-3xl md:text-4xl font-bold text-primary-foreground mb-4">
              Ready to Transform Your Healthcare Management?
            </h2>
            <p className="text-primary-foreground/90 text-lg mb-8 max-w-2xl mx-auto">
              Join thousands of healthcare professionals who trust MedVault for 
              secure, efficient medical record management.
            </p>
            <EnhancedButton 
              variant="hero" 
              size="xl"
              onClick={() => navigate('/login')}
              className="bg-background text-primary hover:bg-background/90 shadow-xl border-0"
            >
              Get Started Today
            </EnhancedButton>
          </div>
        </div>
      </section>

      {/* Footer */}
      <footer id="contact" className="container mx-auto px-4 py-12 border-t border-border bg-primary-pastel">
        <div className="flex flex-col md:flex-row items-center justify-between text-muted-foreground">
          <div className="flex items-center space-x-2 mb-4 md:mb-0">
            <div className="w-8 h-8 bg-gradient-medical rounded-lg flex items-center justify-center shadow-medical">
              <Stethoscope className="w-5 h-5 text-primary-foreground" />
            </div>
            <span className="font-bold text-primary-dark text-lg">MedVault</span>
          </div>
          <div className="text-center mb-4 md:mb-0">
            <p className="text-foreground font-medium mb-1">Contact Us</p>
            <p className="text-sm">info@medvault.com | +1 (555) 123-HEAL</p>
          </div>
          <p className="text-sm text-foreground/70">
            © 2025 MedVault. All rights reserved. HIPAA Compliant Healthcare Management.
          </p>
        </div>
      </footer>

      {/* MedVault Chatbot */}
      <MedVaultChatbot />
    </div>
  );
};

export default Landing;