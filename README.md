Mini VoIP
===

###A compact application for internet voice calls.

---
Info:  

*This is a voice over internet protocol (VoIP) application maintained Evan Ram.*  
*The User Datagram Protocol (UDP) or the Transmission Control Protocol (TCP) may be used.*  
*This application features a peer to peer (p2p) network.*

By default the GUI is used. Command-line mode is activated with the runtime argument `-nogui`.

---

TODO:  

######Primary Objectives

Auto updating
- Press a button in below proposed info menu item
- Replace jar in background or replace certain classes in current jar (similar to `jar uf` command?)

'Info' or 'About' menu item
- Under 'App' menu
- Display information such as application version
- Possibly render an HTML page; certain elements of it can be replaced with online info (such as latest update info)

Transparent GUI
- This was previously mostly working (and it looked decent)
- Not implemented due to rendering issues with the JMenus and JMenuItems while JFrame is transparent

Option for UDP, TCP hole punching
- Users should not have to port forward just to call others
- Server software for others to run their own NAT

SRTP or SRTCP
- Encryption by default, Organized packet system


######Secondary Objectives

Allow user to change audio buffer size
- Had to remove this since the feature was constantly breaking
