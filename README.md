Food Pilot DIU - Food Delivery App for University Campus
Food Pilot DIU is a modern Android application designed to solve a real-world problem for students and faculty at Daffodil International University (DIU). It provides a dedicated platform for ordering food from nearby restaurants for delivery to specific locations within the campus, such as residential halls and faculty rooms.

This project was built as a learning journey into modern Android development, showcasing a complete, feature-rich user application built from the ground up.

The Problem
Students residing in campus hostels, particularly after hours, and faculty members during busy workdays, often face challenges in ordering food from outside restaurants. This app bridges that gap by creating a dedicated delivery ecosystem for the campus community.

Key Features (User App)
Multi-Location Architecture: Scalable design that supports multiple campuses and delivery sub-locations (e.g., "Daffodil Smart City" -> "Hall 1").

Dynamic Home Screen:

Fetches and displays restaurants and promotional offers based on the user's selected location.

Modern, clean UI with an auto-scrolling offer slider and informative restaurant cards.

Swipe-to-refresh functionality to get the latest data.

Advanced Authentication:

Secure sign-up and sign-in using Email/Password.

Seamless one-tap sign-in with Google.

Interactive Restaurant Menus:

View pre-order and currently available food items.

Real-time "Instant Delivery" status indicator controlled by the restaurant.

Users can select items and add them to a temporary basket.

Flexible Ordering Flow:

Add to Cart: Users can add items from a menu to a persistent shopping cart to order later.

Place Order Now: Users can also place an order directly from the menu screen for immediate checkout.

Grouped Shopping Cart: The cart screen intelligently groups selected items by restaurant, each with its own sub-total and "Place Order" button.

Real-time Order Placement: Placing an order creates a new entry in the Firestore database, ready to be picked up by the (future) Vendor App.

Order History: A dedicated screen where users can view a list of their past and current orders with details like date, price, and status.

User Profile Management:

Users can update their name and phone number.

A detailed, dynamic address form allows users to select their Base Location, Sub-Location, and specify building, floor, and room numbers.

Ability to select a profile picture from the device's local gallery, which persists even after the app is closed.

Technologies Used
Language: Kotlin

UI Framework: Jetpack Compose

Architecture: MVVM (Model-View-ViewModel)

Backend: Firebase

Firestore: Real-time NoSQL database for users, restaurants, orders, locations, and offers.

Firebase Authentication: For Email/Password and Google Sign-In.

Image Loading: Coil

Asynchronous Programming: Kotlin Coroutines & Flows

How to Set Up and Run the Project
To get this project running on your own machine, follow these steps:

Clone the Repository:

git clone [your-github-repository-link]

Open in Android Studio: Open the cloned project folder in the latest stable version of Android Studio.

Firebase Setup:

Go to the Firebase Console and create a new project.

Add a new Android app to the project with the package name com.diu.foodpilot.user.

Follow the setup instructions to download the google-services.json file and place it in the app/ directory of the project.

In the Authentication section, enable the Email/Password and Google sign-in providers.

In the Firestore Database section, create a new database and start in Test Mode.

Add SHA-1 Fingerprint:

Follow the instructions in Android Studio to generate your debug SHA-1 key.

In your Firebase Project Settings, add this SHA-1 key to your Android app configuration. This is crucial for Google Sign-In to work.

Re-download the google-services.json file and replace the old one in your project.

Populate Firestore Data:

Manually create the locations, restaurants, and offers collections in your Firestore database, following the structure we developed in the project.

Ensure you use the Document IDs from the locations collection when specifying supportedLocations in your restaurant documents.

Create Firestore Indexes:

Run the app. It will likely fail to load data at first.

Check the Android Studio Logcat. Firebase will provide error messages with direct links to create the necessary composite indexes for your queries.

Click these links and create the indexes. Wait for them to finish building (this can take a few minutes).

Build and Run: Clean and rebuild the project. The app should now run correctly.

This project has been an incredible learning experience, covering everything from basic UI to complex, scalable backend architecture. I hope it serves as a great example of a modern, feature-complete Android application.
