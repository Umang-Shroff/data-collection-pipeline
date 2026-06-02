# Event Ingestion Simulator

## Overview

A Java-based event ingestion simulator that models how analytics platforms receive, store, and analyze user activity events.

Supported event types:

* USER_LOGIN
* APP_OPEN
* PURCHASE
* CAMPAIGN_CLICK

The project demonstrates core Java and backend engineering concepts including:

* Object-Oriented Programming
* Records
* Encapsulation
* Constructor Injection
* Interfaces
* Polymorphism
* Collections Framework
* Event Analytics

## Architecture

Client
→ EventReceiver
→ EventRepository
→ EventStore
→ StatsGenerator

## Features

* Immutable event model using Java Records
* Event ID generation using static sequence
* In-memory event storage
* Event analytics and aggregation
* Event count by type
* Abstraction through EventRepository interface

## Concepts Practiced

### OOP

* Classes
* Objects
* Encapsulation
* Abstraction
* Polymorphism
* Records

### Java Basics

* Constructors
* Access Modifiers
* Packages

### Static

* Static Variables
* Static Methods
* Static Blocks

### Collections

* List
* Map
* Set
* Enhanced For Loops

## Sample Output

Total Events: 4

USER_LOGIN -> 2
APP_OPEN -> 1
PURCHASE -> 1

## Future Improvements

* Persistent storage
* Multithreading support
* REST API endpoints
* Event filtering
* Unit tests
* Database integration
* Kafka integration
