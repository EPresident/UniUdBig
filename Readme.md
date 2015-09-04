# Libraries and examples for JLibBig

This repository holds several example files and libraries to use with [JLibBig](http://mads.dimi.uniud.it/wordpress/downloads/libbig/).
All code is licensed under the GNU GPL version 3.

## Package Overview

- **big**
	- **brs** : strategies for the BRS. I concerns how new nodes of the BSG are computed
	- **bsg** : BigStateGraph is the main structure of the Model Checker
	- **examples** : short, self-contained examples of how to use LibBig
	- **iso** : algorithm for bigraph isomorphism and some examples
	- **matcher** : multi-matcher and property-matcher
	- **mc** : a Model Checker for BRS. It's based on BSG. Some tests are included.
	- **net** : classes for handling bigraphic representations of computer networks
	- **predicate** : predicates for the MC's logic
	- **prprint** : classes for pretty printing Bigraph object from LibBig (making them more human-readable)
	- **rules** : special classes for conserving the properties before and after the application of a rule
	- **sim** : classes to simulate the evolution of a Bigraphic Reactive System (BRS)
- **nmap** : modules to parse network scans made with [Nmap](https://nmap.org/)

## [Wiki](https://github.com/EPresident/UniUdBig/wiki)

## License

Copyright (C) 2015 Elia Calligaris <calligaris.elia@spes.uniud.it> 
and Luca Geatti <geatti.luca@spes.uniud.it>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
