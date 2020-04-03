#Todos.redux

Wouldn't it be great if you could keep track of all your notes? All your posts, tweets, blog entries.  All of your todo lists.  

Wouldn't it be great if you could organize them into collections by hashtag, keyword, whatever? 

Wouldn't it be great if you could search through everything all at once?

It would.

This is a front-end back-end POC of Todos working with a React/Redux front end.  Todos is a back-end modeling framework.  The front end is reworking of the standard todoMVC example from github.com/reacts/redux.  

Redux acts as the client-side MC (Model and Controller) and React acts as the V (View).  Todos on the back-end acts as a parallel Model (and a Controller?), so the resulting system could be called M-MVC, or possibly MC-MVC? 

## What you can do here

* /todos -- main route

* /todos/cdo -- create a Do
* /todos/rdo -- retrieve
* /todos/udo -- update
* /todos/ddo -- delete

A do links to any number of other dos as either parent or child
Children are sorted according to dsort.  If not specified, a new child is appended to the bottom of the list
