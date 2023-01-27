# Uptown Campus

Design Document

Mike Byrd | Jakob Brown | Riley Setser

## Introduction

Did you feel lost during your first year at the university? Do you know where your classes are, what's available for lunch, or what there is to do for fun? Uptown Campus features:  

- Find class location using building name.
- Discover dinning and recreational options
- Add photos of locations as well as brief descriptions  

Enhance your university experience using your Android device. Satisfy your tastebuds with local dinning options. Capture a location using the on-device camera to contribute to the community.  

## Storyboard

![UptownCampusStoryboard](https://user-images.githubusercontent.com/122894342/213880521-5a6ccc2f-1405-4871-badd-ce3e44d8f73c.JPG)

## Functional Requirements

### Requirement 100.0: Search for Location 

**Scenario:** 

As a student I want to find the building my classes are in so that I can attend my classes 

**Dependencies:** 

The location exists and is on campus 

There is internet connection 

The device has GPS capability 

**Assumptions:** 

The location is spelled correctly 

**Examples:** 

1.1

**Given** The building exists 

**When** I search for teacher’s dyer 

**Then** Photos, comments and the location are provided of the Teacher’s Dyer building 

1.2 

**Given** The building exists 

**When** I search for DAAP Cafe 

**Then** Photos, comments and the location are provided of the DAAP Cafe 

1.3 

**Given** The building does not exist or typed incorrectly 

**When** I search for techer’s dier 

**Then** the search result comes up empty 

### Requirement 101.0: Save a photo 

**Scenario:** 

As a student I want to save a photo of a location I just visited so that I can share my experience 

**Dependencies:** 

The device has a camera and GPS capabilities 

There is internet connection 

**Assumptions:**

That the location is the correct one being photographed 

**Examples:** 

2.1 

**Given** There is internet connection 

**When** I take a photo of the Teacher’s Dyer building 

**Then** The photo will begin to post with a prompt to add a comment before posting if I want to 

2.2 

**Given** There is internet connection 

**When** I take a photo of the DAAP Café and begin to post it to the app 

**Then** I will be prompted to add a comment about the DAAP Café before posting 

2.3 

**Given** There is no internet connection 

**When** I take my photo DAAP Café and go to post it on the app 

**Then** The photo will fail to post 

### Requirement 102.0: Save a comment 

**Scenario:**

As a student I want to save a comment on a location so that I can share my thoughts or experience on that location 

**Dependencies:** 

There is internet connection and the device has GPS capabilities 

**Assumptions:**

The location is the correct one being commented on 

**Examples:**

3.1 

**Given** There is internet connection 

**When** I write a comment about the DAAP Café and post it 

**Then** My comment and photo will be posted publicly to the app 

3.2 

**Given** There is internet connection 

**When** I write a comment about the Teacher’s Dyer building and post it 

**Then** My comment and photo will be posted publicly to the app 

3.3 

**Given** There is no internet connection 

**When** I write my comment about nipper stadium and try to post it 

**Then** The post will fail to post to the application 

## Class Diagram

![UptownCampus Class Diagram](/UptownCampus_ClassDiagram.png)

### Class Diagram Description

**MainActivity:** The main screen of the app. It features a map that the user can search for classes and dining locations.\
**RetrofitClientInstance:** Bootstrap class required for Retrofit\
**Building:** Noun class that represent buildings on campus\
**Class:** Noun class that represent classes\
**Dining:** Noun class that represents dining options\
**IBuildingService:** Interface class for BuildingService\
**IClassService:** Interface class for ClassService\
**IDiningService:** Interface class for DiningService\
**BuidingService:** Business logic for buildings\
**ClassService:** Business logic for classes\
**DiningService:** Business logic for dining\
**IClassDAO:** Interface for Room to persist Class data\
**IDiningDAO:** Interface for Room to persist Dining data  

## Scrum Roles  
- Product Owner/Scrum Master/DevOps: Mike Byrd
- Frontend Developer: Riley Setser
- Integration Developer: Jakob Brown

## Weekly Meeting

Thursday at 5pm. Meeting is held on Microsoft Teams in our group chat.
