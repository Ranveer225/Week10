Business Client Database is a simple and friendly application designed to help companies keep track of their clients. 
It allows you to add, edit, and delete information like names, departments, majors, and emails. 
You can also upload and export client data using CSV files, making it easy to share and back up your records. 
The app includes features like light and dark themes, a clear button to reset fields, and even a place to add or change profile pictures. 
It's a great tool for organizing client details and managing them all in one place.
Using the Business Client Database application is simple and straightforward. 
You can add new client records by filling in details like name, email, and department. 
Editing or deleting a record is just as easy with a click of a button. 
The app also supports importing and exporting data through CSV files, so you can share or back up information anytime. 
To customize the interface, you can switch between light and dark themes. 
If you need to change your profile picture, just click on the image area to upload a new one. 
The changes I implemented into this app were rebranding and styling the theme along with adding in new fields for this.
It started off as a student registration form and I replaced the majors with roles as part of a business client system. I added in a clear all button
as well that removes all the data in the form and resets dropdowns. I added in a new button in our interface fxml file for this and connected it with
the controller. Then I added in a method that clears all fields to test this functioning. Next, I added in row editing for table view and the
process of adding rows directly in the table. I had to make each column editable by using TextFieldTableCell and updated the data when editing finishes.
Lastly, I updated the person class so it supports editing by adding constructors and methods properly and added functionality to add a new row.

