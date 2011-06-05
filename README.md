# Synapse - A Scala publishing toolkit from [bheap](http://www.bheap.co.uk) ltd

## What is a publishing toolkit ?

A publishing toolkit enables you to design and develop (in any order you choose) multiple websites and web applications re-using the same components and publishing pipelines.

## The laws of Synapse

1. A view may never deviate from the standard markup site builders understand
2. Keep it simple stupid (KISS)
3. Always use convention over configuration
4. The principle of least surprise must always be followed
5. Everything must be correct
6. DRY

## How Synapse obeys the laws

- Synapse is opinionated
- Lowest common denominator tools are used wherever possible
- Re-use is king, the developers of Synapse are lazy
- Thought always comes first, code later
- Synapse will always be simple
- Synapse will recommend tools you should be using, and will never try to do their job

# Roadmap

- Sites will be built by directory scanning and analysis of mime type of the 'view' files, it will be possible to override settings using xml or json config files
- The structure of a site and its path mounting will be determined by the folder structure of the views for the site, including filename
- HTML views will incorporate parts of the HTML 5 and CSS 3 specs
- HTML views and templates (however they are done) will be validated using xhtml validation and css3 validation
- Designers will have a mechanism to replace the headers and footers across their sites with a tool, this will lead to fully independently viewable 'mockups' and faster runtime.
