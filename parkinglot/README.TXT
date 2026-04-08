PROJECT TITLE:
University Parking Lot Management System (CCP6224 OOAD)

PURPOSE OF PROJECT:
Standalone Java Swing application to manage a multi-level parking lot (entry/exit, tickets, payment, fines, admin views, reports).

VERSION or DATE:
Feb 2026

HOW TO START THIS PROJECT:
Option A (command line):
1) Open a terminal inside the folder that contains the "parkinglot" directory.
2) Compile:
   javac -encoding UTF-8 parkinglot/*.java
3) Run:
   java parkinglot.MainApp

Option B (IDE):
- Open the project folder and run parkinglot.MainApp.

USER INSTRUCTIONS:
- Entry tab: register vehicle, choose suitable spot, generate ticket.
- Exit tab: enter plate -> "Show Receipt" to preview -> "Exit & Pay" to pay (cash/card).
- Admin tab: view all floors/spots, parked vehicles, occupancy, revenue, unpaid fines; set fine scheme (future entries only).
- Reports tab: current vehicles, revenue, occupancy, fine report.
