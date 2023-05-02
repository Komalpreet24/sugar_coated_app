# Blood Glucose Tracking App
This is an Android application built using Kotlin, Firebase for authentication, and Firebase Firestore for database management.
The app is to assist diabteics with the goal of future expansion for pubic use. The main objective of creating this application was to allow family and friends to monitor blood glucose levels and track supplies. Features include recording manual entries of blood sugar level throughout the day, calendar  to track and view events related to diabetes management.

## Features

- **Authentication**: 
  - The app requires users to sign up or log in to access its features. The sign-up page asks for a name, email, and password. The login page asks for the user's email and password. Both pages have a link to switch between them if the user already has an account or needs to create one.
<br>
<p float="left">
  <img src="https://user-images.githubusercontent.com/60284996/235796803-29cc1f28-d765-43fe-8266-8c28e8c7119b.gif" width="250">
  <img src="https://user-images.githubusercontent.com/60284996/235794777-f9979fe9-b9f8-4d44-a475-292c078fa225.jpg" width="250">
</p>

- **Home Page**: 
  - The homepage consists of a webview, which shows a graph of the user's realtime blood glucose readings from a medical device known as CGM (Continous Glucose Monitor).
  - The purpose of the home page is to enable the user to share their readings with friends and family.
  - The home page also has an "Add" button that allows users to add values of blood glucose checked manually through a glucometre throughout the day.
  - The home page also displays when the user's next CGM change (sensor) or Insulin delivery device (infusion set) change is due, serving as a reminder to help manage diabetes care.
<br>
<p float="left">
  <img src="https://user-images.githubusercontent.com/60284996/235792641-b4ed3aac-2f6f-496e-b24a-b4c0ecf8bcff.jpg" width="250">
  <img src="https://user-images.githubusercontent.com/60284996/235797450-f63a4aa6-e86b-43e8-955c-b53d83999c6a.gif" width="250">
</p>
  
- **Add New Record Page**:
  - The "Add New Record" page allows the user to log a new blood glucose reading along with a category that can be selected from a set of user-friendly icons.
  - The user can also select the date and time of the reading using a date and time picker. By default, the current date is pre-selected and the user cannot select a date in the future.
  - In addition to the blood glucose reading and category, the user can also add an optional note to provide more information about the reading. Once the user has entered all the required information, they can save the record by clicking on the "Save" button.
<br>
<p float="left">
  <img src="https://user-images.githubusercontent.com/60284996/235794099-5334168f-fda3-4f8e-a3f3-4c4c322c5f7d.jpg" width="250">
</p>

- **Supplies Page**:
  - This page has a calendar that marks two events which are repeated after a fixed duration of days. The sensor and infusion set are needed to be changed every 4 and 14 days respectively. To better understand and be able to plan ahead when the user's changes would be due, each change event is marked on the calender, which will also serve as a reminder.
  - On click of today's date, a dialog comes up in which the user can log either of those events, and the upcoming events will sort themselves accordingly. 
  - The page also allows the user to track the number of supplies (sensors and infusion sets) remaining and provides information on how long the supply will last. Using the "Add Supplies" button the user can input and manage the number of supplies. When an event is marked, the number of supplies is also subtracted. 
<br>
<p float="left">
  <img src="https://user-images.githubusercontent.com/60284996/235794266-a5a79c3e-681b-411e-bd00-93d6a79492c8.jpg" width="250">
  <img src="https://user-images.githubusercontent.com/60284996/235798067-450484ef-b5fc-4d0e-8fa2-3285ebd15a60.gif" width="250">
  <img src="https://user-images.githubusercontent.com/60284996/235798447-189ecb5a-c702-462b-a30b-60afc23fc4d7.gif" width="250">
</p>

- **LogBook Page**:
  - This page shows a list of all records from latest to oldest. Each record has a category icon next to it for more information on the logged value with time and date and an additional note.
<br>
<p float="left">
  <img src="https://user-images.githubusercontent.com/60284996/235790826-a9b5bc49-6e60-411d-81b5-0c3f2084aea9.jpg" width="250">
</p>

- **Settings Page**: 
  - This page allows users to customise the colors for calendar events and set the target range for manual entries.The weblink for tracking user's blood glucose value should also be added here,
<br>
<p float="left">
  <img src="https://user-images.githubusercontent.com/60284996/235794545-dc49be8f-e493-4b6f-9087-6bffca09abd8.jpg" width="250">
  <img src="https://user-images.githubusercontent.com/60284996/235794492-9f9e1ef6-2fa6-4767-b241-e17dd9158ca9.jpg" width="250">
</p>
  
  
## Tech Stack
- Kotlin
- MVVM Architecture
- Firebase (Authentication)
- Firestore (Database Management)
## Installation
- Clone the repository: `git clone https://github.com/Komalpreet24/sugar_coated_app.git`
- Open the project in Android Studio
- Build and run the project
