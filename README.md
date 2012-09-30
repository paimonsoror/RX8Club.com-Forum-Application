RX8Club.com-Forum-Application
=============================

Purpose:
Source code for an android based application that allows users to 
interface with the RX8Club.com forums

Design:
This application is designed primarily for use with Android based
phones, but has also been tested with Android tablets and Android
virtual machines

Contents:
You will find six (6) total packages in this project

com.normalexception.forum.rx8club - The main package which contains 
a single class that starts the application process.  This class also
contains a static reference to the application context

com.normalexception.forum.rx8club.activities - A list of specific 
activities that carry on tasks within the application.  These activities
are implementations of the project's ForumBaseActivity interface which
extends the Android SDK Activity class

com.normalexception.forum.rx8club.handler - Contains common handlers
used within the application

com.normalexception.forum.rx8club.prompts - A useful class that can be
used to prompt the user for an input

com.normalexception.forum.rx8club.utils - A set of common utilities to be
used in the project

com.normalexception.forum.rx8club.view - A set of view related classes

Needs:
Thread post date/time