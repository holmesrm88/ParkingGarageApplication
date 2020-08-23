Parking Garage Application - A Java Spring Boot application for a parking garage payment system that determines how much
to charge a car as it's pulling out of the parking garage based on how long it was parked.

Input:
1. A .csv file with headers and data
2. Each row in the CSV file represents a specific car either pulling in or out of the parking garage.
3. This CSV is separated by commas ‘,’ so if you require a different delimiter for your locale please let us know.

Expected Outcome:
1.	An entry for each time a car pulls out what they would have been charged. This needs to include the license plate
number, departure time, and charge
2.	Total number of cars that parked that month
3.	Total amount of money charged per license plate
4.	Total amount of money made across all cars

Required tools for testing:
Postman
Maven installed

Two ways to run the program:
1)From an IDE
-Import the project into an IDE.
-Compile and Start the project.
-Open Postman
-Create a POST Request with URL http://localhost:8080/upload-csv-file
-In the Body, select "file" as the key, and select the .csv file for the value.
-Hit Send
-Results will be posted in root directory of the project under the name ParkingGarageMonthlyOutput.txt

2) From a command line
-Open terminal
-'cd' into the directory where the project is placed
-Execute the command 'mvn clean install'
-Execute the command 'mvn spring-boot:run'
-Open Postman
-Create a POST Request with URL http://localhost:8080/upload-csv-file
-In the Body, select form-data, select "file" as the key from the key dropdown and enter 'file' as key,
and select the .csv file for the value.
-Hit Send
-Results will be posted in root directory of the project under the name ParkingGarageMonthlyOutput.txt

Assumption:
1) This is a program which would be ran monthly. As vehicles enter and leave the parking garage, a system will collect their information; similar to a smart tag.  Once a month, a .csv file will be fed to this application with the required information to determine charges.
2) When a person deposits funds for prepaid parking, this amount is counted as money earned for the month.
3) This program will be ran once a month.  After the program is completed, the in-memory database is cleared to save on
size.  In a production system, a flag in the database would be used to indicate the transaction has been accounted for.
4) If a vehicle has a parking pass, it will show as $0.00 for the charge.
5) If a vehicle has a positive balance that is greater than the charge in their prepaid account, it will show as $0.00 for the charge.
6) Port 8080 is not in use.
7) Total number of cars parked does not mean total distinct cars parked.  It means each time a car parked and left.

