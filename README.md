Hi Students,
Further to the recent communication regarding Coursework Scenario 2, I would like to provide structured conceptual guidance to help you understand how to approach the problem correctly from a concurrent programming perspective. As stated previously, Scenario 2 is fundamentally a Producer–Consumer problem. This is not a new concept introduced at the last moment; rather, it is a pattern we have been analysing and solving from the early weeks of the module.
Please note carefully:

The steps outlined below describe one valid way to reason about and implement the solution, but this is not the only acceptable approach. In addition, a detailed, deep research–enabled technical evaluation of a reference implementation for Question 02 is attached separately for your academic understanding and benchmarking purposes.
  1. Understanding the Problem (Conceptual Breakdown)
The core issue described in the scenario is that the existing system is single-threaded (sequential), which becomes a bottleneck during peak load:
Patients accumulate in queues
Consultants remain underutilised
The 4-hour target is breached
From the marking scheme, you are explicitly assessed on three areas:

(a) Continuous Patient Arrivals (10 marks)
Patients arrive randomly, 24/7
Arrivals must continue while consultants are treating patients
Each patient belongs to exactly one speciality

(b) Automated Simulated Shift Management (10 marks)
Two shifts per day: Day (12h) and Night (12h)
Each shift has three consultants (one per speciality)
Shift rotation must be automatic
Handover must be smooth, with no patient loss

(c) Concurrent Processing (10 marks)
Multiple consultants must work simultaneously
Each consultant treats only patients of their own speciality
Queue management must be thread-safe (no race conditions)

  2. Mapping the Scenario to a Concurrency Pattern
This scenario maps directly to a Producer–Consumer system:
Producer(s): Patient arrival generator(s)
Consumer(s): Consultant worker threads
Shared buffer: Thread-safe patient queue(s)
Given that there are only three specialities, a simple and correct design is to use three separate thread-safe queues, one per speciality. This:
Guarantees correct matching automatically
Reduces contention compared to a single shared queue

  3. Simulation Design Decisions
A shift represents who is on duty, not where patients are stored
Patient queues must persist across shift changes
Only consultant threads are stopped and restarted
Since real-time simulation of 24 hours is impractical, you must define a time scale, for example:
1 simulated hour = 1 real second
12-hour shift = 12 seconds
Any consistent scale is acceptable, provided it is stated clearly.

  4. Implementation-Level Guidance (High-Level)
A sound implementation typically includes:
A PatientGenerator thread that continuously creates patients at random intervals
BlockingQueue<Patient> for each speciality
Consultant worker threads that:
Block safely when queues are empty
Process patients concurrently

A ShiftManager that:
Automatically rotates shifts after the simulated duration
Stops old consultant threads safely
Starts new consultant threads immediately
Ensures queues are not reset
Proper logging is strongly recommended to demonstrate:
Patient arrivals during processing
Concurrent treatment by multiple consultants
Shift start/end events
Absence of patient loss

5. Important Reminder
This guidance is intended to help you reason about the problem correctly and align your solution with the marking criteria.
It does not replace the requirement for you to:
Implement your own solution
Understand and justify your design decisions
Demonstrate correct concurrent behaviour in your code

6. Purpose of Each Class

1. Patient

Purpose:
Represents a patient entering the hospital system.

What it holds (from code):

Patient ID
Speciality (as a String)
Arrival timestamp
Concurrency role:

Simple data object
Passed between threads via queues
❌ Not a producer
❌ Not a consumer
 

2. PatientQueue

Purpose:
Stores waiting patients for one speciality.

Key implementation detail:

Internally uses LinkedBlockingQueue<Patient>
Concurrency role:
✅Shared Resource

Why:

Written to by the producer thread
Read from by consultant threads
Thread-safe access guaranteed by BlockingQueue
 

3. PatientArrival (Runnable)

Purpose:
Continuously generates patients.

Concurrency role:
✅ Producer

What it does (from code):

Runs in its own thread
Creates patients at random time intervals
Assigns a speciality
Adds patients to the correct PatientQueue
Why it exists:

Ensures continuous arrivals, even while consultants are busy
 

4. Consultant (Runnable)

Purpose:
Simulates a doctor treating patients.

Concurrency role:
✅ Consumer

What it does (from code):

Runs as a separate thread
Takes patients from one PatientQueue using take()
Simulates treatment using sleep()
Processes patients concurrently with other consultants
Why it exists:

Enables parallel treatment
Ensures each consultant handles only one speciality
 

5. ShiftManager

Purpose:
Controls consultant threads for day/night shifts.

Concurrency role:
❌ Not a producer
❌ Not a consumer

What it does:

Starts consultant threads for a shift
Stops consultant threads at shift end using interrupt()
Alternates shifts automatically
Ensures patient queues persist across shifts
Why it exists:

Implements automated shift management
Prevents patient loss during handover
 

6. HospitalSimulation (Main Class)

Purpose:
Coordinates the entire simulation.

What it does:

Creates patient queues
Starts the PatientArrival producer thread
Invokes ShiftManager
Stops threads cleanly
Prints final statistics
Concurrency role:

Controller / bootstrap class
❌ Not producer
❌ Not consumer
