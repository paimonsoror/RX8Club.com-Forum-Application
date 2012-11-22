RX8Club.com-Forum-Application
=============================

Purpose:
-------------------------
Source code for an android based application that allows users to 
interface with the RX8Club.com forums

Design:
-------------------------
This application is designed primarily for use with Android based
phones, but has also been tested with Android tablets and Android
virtual machines

Contents:
-------------------------
You will find nine (12) total packages in this project

*com.normalexception.forum.rx8club* - The main package which contains 
a single class that starts the application process.  This class also
contains a static reference to the application context

*com.normalexception.forum.rx8club.activities* - A list of specific 
activities that carry on tasks within the application.  These activities
are implementations of the project's ForumBaseActivity interface which
extends the Android SDK Activity class

*com.normalexception.forum.rx8club.activities.fragments* - Handler for common
view fragments

*com.normalexception.forum.rx8club.activities.list* - Activities that represent
category and post lists

*com.normalexception.forum.rx8club.activities.pm* - Activities that represent 
private messages and related functions

*com.normalexception.forum.rx8club.activities.thread* - Activities that represent
threads and their related functions

*com.normalexception.forum.rx8club.enums* - A set of enumerations used
by the source

*com.normalexception.forum.rx8club.handler* - Contains common handlers
used within the application

*com.normalexception.forum.rx8club.preferences* - Contains the xml file 
that defines the application preferences

*com.normalexception.forum.rx8club.task* - Contains the submit task that handles
when a user presses submit in a thread

*com.normalexception.forum.rx8club.utils* - A set of common utilities to be
used in the project

*com.normalexception.forum.rx8club.view* - A set of view related classes

Needs:
-------------------------
* Add pagination to the top of threads, allow preference to enable/disable
* Fix search results, clicking on a link takes to first page instead of most recent
* Pinch to Zoom