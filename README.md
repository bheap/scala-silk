# SILK

## About

A Scala publishing toolkit from [bheap](http://www.bheap.co.uk) ltd

## What is a publishing toolkit ?

A publishing toolkit enables you to design and develop (in any order you choose) multiple websites, web applications and web services re-using the same components and publishing pipelines.

## The laws of Silk

1. A view may never deviate from the markup site builders understand (HTML (4-5) and CSS (2.1-3)), or xml/json for web services
2. Keep it simple stupid (KISS), not just simple but also intuitive
3. Always use convention over configuration (default for everything)
4. The principle of least surprise must always be followed (always let the users know what to expect)
5. Everything must be correct (receive liberally, give strictly)
6. DRY (yeah, we are all lazy)
7. One or a million?  Do you want your processes to run once or a million times ?  Remember very little needs to be dynamic.

## How Silk obeys the laws

- Lowest common denominator tools are always chosen first, the right tool for the job
- Re-use is king, the developers of Silk are lazy
- Thought always comes first, code later
- Silk will always be simple and intuitive
- Silk will recommend tools you should be using, and will never try to do their job
- Caching is activated and aggressive by default, things only change when they need to
- Silk is opinionated

## Getting started

### Installing

    >./silk_install.sh

### See which site prototypes are available

    >silk sites

### Clone a site prototype

    >silk clone somesite

### Spin your site

    >silk spin

### What now ?

Have a poke around in your generated Silk site.

    >open site/index.html

### Templates

Most sites have at least one template, with a design including headers, footers, navigation etc.  This is where we start.  In your generated Silk site under 'template' there is a default template prepared for you called 'default.html'.  As indicated by its extension and mime-type this template is for html views.

Do whatever you like in here as long as it is valid xhtml *and* contains a div tag with id 'silk-template'.

The content of your individual views will be written into this div tag by our template mix in tool.

Note, if you don't want templates, you don't have to use them, just build your views in a little more detail :-)

### Views

Without a view, or views, there is no website or webapp or set of web services.  Views are the epicentre with Silk.

In Silk we like like all views to be constructed so they render independently from the template.  Start with valid html and body elements, then ensure everything you want to be pulled in as the view is in a div tag with id 'silk-view'.

### Components

Components provide a means to build sites out of reusable lego like blocks of content.  Your Silk generated site may have an example of a component.  Again components start with a valid html and body element, and contain an element with an id something like 'silk-component:static/simple'.

This specifies a component which exists in a directory called 'static' which is itself called 'simple'.

## Features

- views are usable outside of Silk as complete renderable entities (when working with html views)
- no special markup or tags

## Roadmap

- Sites will be built by directory scanning and analysis of mime type of the 'view' files, it will be possible to override settings using xml or json config files
- The structure of a site and its path mounting will be determined by the folder structure of the views for the site, including filename
- HTML views will incorporate parts of the HTML 5 and CSS 3 specs
- HTML views and templates (however they are done) will be validated using xhtml validation and css3 validation
- Views will be mounted depending on the mime type, it will be assumed xhtml is root, json and xml will be 'rest', this can be overridden

## Braindump

This section covers thoughts I am not ready to consolidate yet.  Ideas for the functioning of Silk as a product on a high level.

### Convention over configuration

- top level defaults are to be provided in .silk in the users home folder (this enables multi site re-use and sensible defaults for serialisers, doctypes and compatibility components)
- components specified in views will be searched for in local component folders, then in .silk
- config will be specified at top level in .silk, and then overridable by the setup mechanism for a new site 'create' user will be prompted to override default settings in CLI interface
