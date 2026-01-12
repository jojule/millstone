![Millstone Web Application Framework](examples/WEB-INF/src/org/millstone/examples/features/millstone-logo.gif)

> **Historical Archive**: This repository contains the archived source code of the Millstone framework (2000-2006), which later evolved into the [Vaadin Framework](https://vaadin.com).

[![License: LGPL v2.1](https://img.shields.io/badge/License-LGPL%20v2.1-blue.svg)](https://www.gnu.org/licenses/lgpl-2.1)
[![Java](https://img.shields.io/badge/Java-J2EE-orange.svg)](https://www.oracle.com/java/)

## About Millstone

Millstone was a pioneering Java-based user interface library for networked application development, developed by IT Mill Ltd. It introduced an innovative server-side UI component model that enabled rapid application development with terminal/browser independence.

### The Evolution to Vaadin

Millstone laid the foundation for what would become one of the most successful Java web frameworks:

- **2000**: Development started
- **2001**: First production use of Millstone Library
- **2002**: Released as open source under LGPL license
- **2006**: Development of next generation (IT Mill Toolkit 4)
- **2007**: Renamed to IT Mill Toolkit, integrated with GWT
- **2009**: Renamed to **Vaadin Framework** (May 20, 2009 and released as Vaadin 6)
- **Present**: Continues as [Vaadin](https://vaadin.com), a leading full-stack web development platform for Java

> 💡 **Learn more about Vaadin**: Visit [vaadin.com](https://vaadin.com) to see how Millstone's revolutionary concepts evolved into a modern, production-ready framework used by thousands of companies worldwide.

## Key Features

### Revolutionary UI Component Model

- **Server-side component framework** with powerful data model and event handling
- **Terminal/Browser independent** - write once, deploy to web, mobile, and desktop
- **Reusable UI components** with true component reusability
- **Clean, Swing-like API** focused on ease of learning and consistency
- **Separation of concerns** - UI logic separated from presentation

### Technical Highlights

- One of the first (if not the first) UI component based web framework
- Based on Java and J2EE
- UIDL (User Interface Definition Language) for terminal abstraction
  - Both XSLT-based and Ajax rendering
    - Themeable presentation layer
  - Supports multiple terminal adapters (Web, WAP, MIDP, J2PE); but only Web is included in the Open Source project
- Open Source (LGPL 2.1)

## Architecture

### Millstone in J2EE Architecture

```
┌─────────────────┐    ┌──────────────────────┐    ┌─────────────┐    ┌──────────────┐
│  Client-Side    │    │   Server-Side        │    │   Business  │    │  Enterprise  │
│  Presentation   │    │   Presentation       │    │   Logic     │    │  Information │
│                 │    │                      │    │             │    │   Systems    │
├─────────────────┤    ├──────────────────────┤    ├─────────────┤    ├──────────────┤
│                 │    │   Web Server         │    │             │    │              │
│  Browser        │◄──►│                      │    │    EJB      │◄──►│   Database   │
│  - HTML         │    │  Millstone App       │◄──►│  Container  │    │              │
│  - JavaScript   │    │  ┌────────────────┐  │    │             │    │              │
│  - Applet       │    │  │ Theme │ UI     │  │    │   EJBs      │    │              │
│                 │    │  │       │ Logic  │  │    │             │    │              │
│  Desktop        │    │  └────────────────┘  │    │             │    │              │
│  - Java App     │    │                      │    │             │    │              │
│                 │    │  Servlet / JSP       │    │             │    │              │
│  Mobile/PDA     │    │                      │    │             │    │              │
│  - WAP          │    │                      │    │             │    │              │
│  - Midlet       │    │                      │    │             │    │              │
└─────────────────┘    └──────────────────────┘    └─────────────┘    └──────────────┘
```

### Application Structure

The Millstone architecture consists of five main layers:

1. **Data Storage** - Relational databases, file systems, and business logic (EJB)
2. **Data Access** - Data source interfaces and base data sources
3. **User Interface** - Millstone UI components (base and third-party)
4. **Terminal Communication** - Terminal adapters and themes for different devices
5. **Client Terminals** - Web browsers, desktop applications, and mobile devices

```
┌──────────────────────────────────────────────────────────────────┐
│                    Web Server / Servlet Container                │
│                                                                  │
│  ┌──────────────┐   ┌──────────────────┐   ┌──────────────────┐  │
│  │ Data Source  │   │  UI Components   │   │ Terminal Adapter │  │
│  │ Interfaces   │   │                  │   │                  │  │
│  │              │   │  - Base          │   │  - Web (HTML)    │  │
│  │ - Base Data  │   │  - Third Party   │   │  - WAP           │  │
│  │   Sources    │   │                  │   │  - MIDP          │  │
│  │ - Third      │   │                  │   │                  │  │
│  │   Party      │   │                  │   │  Base Theme      │  │
│  └──────────────┘   └──────────────────┘   │  Third Party     │  │
│                                            │  Themes          │  │
│  ┌────────────────────────────────────┐    └──────────────────┘  │
│  │     Application Logic              │                          │
│  │                                    │                          │
│  └────────────────────────────────────┘                          │
│                           ▲                                      │
└───────────────────────────┼──────────────────────────────────────┘
                            │
                    ┌───────┴────────┐
                    │ UIDL Protocol  │
                    └───────┬────────┘
                            ▼
                    ┌────────────────┐
                    │ Client Device  │
                    └────────────────┘
```

### UI Component Revolution

Millstone brought client-server UI programming concepts to server-side web applications:

```
┌─────────────────────────────────────────┐
│           Terminal Adapter              │
│                   │                     │
│                 UIDL                    │
│                   │                     │
│  ┌────────────────▼──────────────────┐  │
│  │         User Session              │  │
│  │  ┌─────────────────────────────┐  │  │
│  │  │         Window              │  │  │
│  │  │  ┌───────────────────────┐  │  │  │
│  │  │  │       Panel           │  │  │  │
│  │  │  │  ┌─────────┐ ┌──────┐ │  │  │  │
│  │  │  │  │  Table  │ │Label │ │  │  │  │
│  │  │  │  └────┬────┘ └───┬──┘ │  │  │  │
│  │  │  └───────┼──────────┼────┘  │  │  │
│  │  └──────────┼──────────┼───────┘  │  │
│  └─────────────┼──────────┼──────────┘  │
│                │          │             │
└────────────────┼──────────┼─────────────┘
                 │          │
         ┌───────▼──────────▼───────┐
         │    Business Logic        │
         └──────────┬───────────────┘
                    │
         ┌──────────▼───────────────┐
         │     Data Storage         │
         └──────────────────────────┘
```

### Terminal Freedom

Write once, deploy everywhere - the Millstone way:

```
┌─────────────────────────────────────────┐
│   Millstone Base UI Components          │
│   (Terminal Independent Java API)       │
└──────────────────┬──────────────────────┘
                   │
         ┌─────────▼─────────┐
         │ Terminal          │
         │ Independent       │
         │ UIDL              │
         └─────────┬─────────┘
                   │
    ┌──────────────┼──────────────┐
    │              │              │
┌───▼───┐      ┌───▼───┐      ┌──▼────┐
│  WEB  │      │  WAP  │      │  MIDP │
│Adapter│      │Adapter│      │Adapter│
└───┬───┘      └───┬───┘      └───┬───┘
    │              │              │
┌───▼───┐      ┌───▼───┐      ┌── ▼───┐
│Browser│      │ WAP   │      │ Mobile│
│(HTML/ │      │Device │      │  Java │
│ JS)   │      │       │      │       │
└───────┘      └───────┘      └───────┘
```

## Value Proposition

### For Developers

- 🚀 **Rapid application development** with reusable components
- 📚 **Fast learning curve** - familiar Swing-like API
- 🔧 **No terminal-specific knowledge** required
- 🎨 **Separation of UI logic and presentation** through themes
- ♻️ **True component reusability** across projects

### For Organizations

- 💰 **Lower development and maintenance costs**
- ⚡ **Faster development process**
- 🎯 **Better quality with less know-how**
- 🔓 **Open development model** - vendor independence
- 📱 **Multi-device support** without extra effort

## Technology Stack

- **Language**: Java (J2EE)
- **UI Protocol**: UIDL (User Interface Definition Language)
- **Presentation**: XML/XSL transformations
- **Server**: Servlet/JSP containers
- **Standards**: Based on Java and XML industry standards

## License

This project is licensed under the **GNU Lesser General Public License v2.1** (LGPL-2.1).

The LGPL license allows:
- ✅ Use in commercial applications
- ✅ Modification and distribution
- ✅ Linking with proprietary software
- ✅ Patent grant from contributors

See the [LICENSE](LICENSE) file for the full license text.

## Historical Context

### About IT Mill Ltd

Millstone was developed by IT Mill Ltd (later renamed Vaadin Ltd), a Finnish company specializing in enterprise web application frameworks. The company's tagline was "Interfacing IT" - reflecting their mission to create better interfaces between users and information systems.

### Why It Mattered

In the early 2000s, web development was fragmented:
- Complex, error-prone JavaScript required for interactivity
- Separate codebases for different browsers
- No standardized component model
- Mixing of presentation and business logic

Millstone solved these problems by:
- Moving UI logic to the server (Java)
- Providing a clean component abstraction
- Separating presentation (themes) from logic
- Offering terminal independence

## Project Status

**This is a historical archive**. The Millstone framework is no longer actively developed.

For modern web application development with Java, please use **[Vaadin](https://vaadin.com)**, the direct successor to Millstone, which continues the same philosophy with modern technology.

## Repository Information

This repository was converted from the original CVS repository, preserving:
- Commit history (2002-2006)
  - Earlier proprietary work startig from 2000 was not imported commit by commit
- All branches and tags
- Original committer information

### Branches

- `master` - Main development branch
- `version-3_0` - Version 3.0 maintenance
- `AjaxText`, `dyncoltable`, `field` - Feature branches

### Tags

Version releases from 3.0.0-pre3 through 3.1.1, documenting the framework's evolution.

## Documentation

Historical documentation can be found in the `docs/` directory. For API documentation, see the JavaDoc in the source code.

## Building

This is a historical codebase using Ant build system:

```bash
cd base
ant
```

Requirements:
- Java 1.4.2 or later (from that era)
- Apache Ant
- Servlet API 2.3+

## Examples

The `examples/` directory contains sample applications demonstrating:
- Hello World
- Calculator
- Chat application (server push)
- Feature Browser (comprehensive component showcase)

## Community

While this project is archived, you can:
- Report historical information or corrections via GitHub Issues
- Discuss Millstone history on the [Vaadin Forums](https://vaadin.com/forum)
- Learn about Vaadin's modern features at [vaadin.com](https://vaadin.com)

## Acknowledgments

This framework was made possible by:
- **IT Mill Ltd** (now Vaadin Ltd) - Original development
- **Tekes** - Research funding
- **The open source community and customers** - Ideas, testing, feedback, and contributions
- **All contributors** - See the commit history for individual contributions

## Links

- Modern version of Millstone: [Vaadin](https://vaadin.com)
  - [Documentation](https://vaadin.com/docs)
  - [Source Code](https://github.com/vaadin)
  - [Discussion forum](https://vaadin.com/forum)
- Original Millstone SourceForge repository: [sourceforge.net/projects/millstone](https://sourceforge.net/projects/millstone/)
