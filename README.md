# SYNAPSE

## About

A Scala publishing toolkit from [bheap](http://www.bheap.co.uk) ltd

## What is a publishing toolkit ?

A publishing toolkit enables you to design and develop (in any order you choose) multiple websites and web applications re-using the same components and publishing pipelines.

## The laws of Synapse

1. A view may never deviate from the standard markup site builders understand (HTML (4-5) and CSS (2.1-3))
2. Keep it simple stupid (KISS), not just simple but also intuitive
3. Always use convention over configuration (default for everything)
4. The principle of least surprise must always be followed (always let the users know what to expect)
5. Everything must be correct (receive liberally, give strictly)
6. DRY (yeah, we are all lazy)

## How Synapse obeys the laws

- Lowest common denominator tools are always chosen first
- Re-use is king, the developers of Synapse are lazy
- Thought always comes first, code later
- Synapse will always be simple and intuitive
- Synapse will recommend tools you should be using, and will never try to do their job
- Synapse is opinionated

## Getting started

This section will walk you through the example site, under ~/.synapse/sites/bheap-example

### Templates

Most sites have at least one template, with a design including headers, footers, navigation etc.  This is where we start.  Under ~/.synapse/sites/bheap-example/templates there is a default template prepared for you called 'default.html'.  As indicated by its extension and mime-type this template is for html views.

Do whatever you like in here as long as it is valid xhtml *and* contains a div tag with id 'synapse:view'.

The content of your individual views will be written into this div tag by our template mix in tool.

Note, if you don't want templates, you don't have to use them, just build your views in a little more detail :-)

### Views

Without a view, or views, there is no website or webapp.  Views are the epicentre with Synapse.

## Setup

So, now we understand Synapse templates and views, how do we run our site ?  Simple, we merge the template into our views with a simple tool 'GiftWrap'.

    >cd $SYNAPSE_HOME
    >sbt console
    >import com.bheap.synapse.tools.GiftWrap
    >val gw = new GiftWrap("default.html", "html")
    >gw.wrap

Now check your views... peachy hey ?  You can do this as many times as you like... just update your template, and repeat.

Now, lets run the site.

    >cd $SYNAPSE_HOME
    >sbt run

## Features

- views are usable outside of Synapse as complete renderable entities
- no special markup or tags

## Roadmap

- Sites will be built by directory scanning and analysis of mime type of the 'view' files, it will be possible to override settings using xml or json config files
- The structure of a site and its path mounting will be determined by the folder structure of the views for the site, including filename
- HTML views will incorporate parts of the HTML 5 and CSS 3 specs
- HTML views and templates (however they are done) will be validated using xhtml validation and css3 validation
- Views will be mounted depending on the mime type, it will be assumed xhtml is root, json and xml will be 'rest', this can be overridden
