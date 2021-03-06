## Keychain2Pass
Keychain2Pass is a program written in Scala which can be use to migrate your OSX Keychain to the password manager [pass](https://www.passwordstore.org).

### Requirements
_Java version >= 8_

[Java](http://www.oracle.com/technetwork/java/javase/downloads/index.html) is pre-installed on a Mac with OSX version 10.11.


_Scala version >= 2.12.2_

[Scala](http://www.scala-lang.org) can be installed via [brew](https://brew.sh).
 
    brew install scala


### Usage

[Download](https://github.com/mbauhardt/keychain2pass/releases/latest) the latest distribition.
Execute the executable jar file.

Usage:
 
    scala keychain2pass_2.12-0.1.2.jar
    Usage: scala keychain2pass_2.12-0.1.2.jar [-h | -enc <ENCODING>]


Help:

    scala keychain2pass_2.12-0.1.2.jar -h
    Usage: scala keychain2pass_2.12-0.1.2.jar -enc <ENCODING>
    
    Where <enc> is one of the following values: [Big5, Big5-HKSCS, CESU-8, EUC-JP, EUC-KR, GB18030, GB2312, GBK, IBM-Thai, IBM00858, IBM01140, IBM01141, IBM01142, IBM01143, IBM01144, IBM01145, IBM01146, IBM01147, IBM01148, IBM01149, IBM037, IBM1026, IBM1047, IBM273, IBM277, IBM278, IBM280, IBM284, IBM285, IBM290, IBM297, IBM420, IBM424, IBM437, IBM500, IBM775, IBM850, IBM852, IBM855, IBM857, IBM860, IBM861, IBM862, IBM863, IBM864, IBM865, IBM866, IBM868, IBM869, IBM870, IBM871, IBM918, ISO-2022-CN, ISO-2022-JP, ISO-2022-JP-2, ISO-2022-KR, ISO-8859-1, ISO-8859-13, ISO-8859-15, ISO-8859-2, ISO-8859-3, ISO-8859-4, ISO-8859-5, ISO-8859-6, ISO-8859-7, ISO-8859-8, ISO-8859-9, JIS_X0201, JIS_X0212-1990, KOI8-R, KOI8-U, Shift_JIS, TIS-620, US-ASCII, UTF-16, UTF-16BE, UTF-16LE, UTF-32, UTF-32BE, UTF-32LE, UTF-8, windows-1250, windows-1251, windows-1252, windows-1253, windows-1254, windows-1255, windows-1256, windows-1257, windows-1258, windows-31j, x-Big5-HKSCS-2001, x-Big5-Solaris, x-COMPOUND_TEXT, x-euc-jp-linux, x-EUC-TW, x-eucJP-Open, x-IBM1006, x-IBM1025, x-IBM1046, x-IBM1097, x-IBM1098, x-IBM1112, x-IBM1122, x-IBM1123, x-IBM1124, x-IBM1166, x-IBM1364, x-IBM1381, x-IBM1383, x-IBM300, x-IBM33722, x-IBM737, x-IBM833, x-IBM834, x-IBM856, x-IBM874, x-IBM875, x-IBM921, x-IBM922, x-IBM930, x-IBM933, x-IBM935, x-IBM937, x-IBM939, x-IBM942, x-IBM942C, x-IBM943, x-IBM943C, x-IBM948, x-IBM949, x-IBM949C, x-IBM950, x-IBM964, x-IBM970, x-ISCII91, x-ISO-2022-CN-CNS, x-ISO-2022-CN-GB, x-iso-8859-11, x-JIS0208, x-JISAutoDetect, x-Johab, x-MacArabic, x-MacCentralEurope, x-MacCroatian, x-MacCyrillic, x-MacDingbat, x-MacGreek, x-MacHebrew, x-MacIceland, x-MacRoman, x-MacRomania, x-MacSymbol, x-MacThai, x-MacTurkish, x-MacUkraine, x-MS932_0213, x-MS950-HKSCS, x-MS950-HKSCS-XP, x-mswin-936, x-PCK, x-SJIS_0213, x-UTF-16LE-BOM, X-UTF-32BE-BOM, X-UTF-32LE-BOM, x-windows-50220, x-windows-50221, x-windows-874, x-windows-949, x-windows-950, x-windows-iso2022jp]

The parameter encoding is there to define with which encoding the passwords are stored within OSX keychain.
If you are not sure what your encoding is, choose UTF-8. 

E.g. Migrate passwords stored with encoding ISO-8859-15

    scala keychain2pass_2.12-0.1.2.jar -enc ISO-8859-15
    
    
    Migrate keychain /Users/marko/Library/Keychains/login.keychain                                                                                                                       
    ===========================================================================================================                                                                       
    [0%] Successfully added 'AIM' to pass folder '/Users/marko/Library/Keychains/login.keychain/Apps/AIM'                                                                   
    [51%] Successfully added 'my_notes' to pass folder '/Users/marko/Library/Keychains/login.keychain/Notes/my_notes'                                                           
    [100%] Successfully added 'github.com' to pass folder '/Users/marko/Library/Keychains/login.keychain/Websites/github.com'            
    
    
    Migrate keychain /Library/Keychains/System.keychain
    ===========================================================================================================
    [41%] Successfully added 'TP-LINK_Extender_B123' to pass folder '/Library/Keychains/System.keychain/Wifi/TP-LINK_Extender_B123'
    [91%] Successfully added 'Airport Express Network' to pass folder '/Library/Keychains/System.keychain/Wifi/Airport-Express-Network'
    [100%] Successfully added 'EasyBox-123' to pass folder '/Library/Keychains/System.keychain/Wifi/EasyBox-123'


The migrated osx keychain is saved under ~/.password-store and can be viewed via `pass`  

    marko:~/labs/keychain2pass git:(master* # bd094ff # origin/master)                                                                                         
    [Sun 16, 22:02] 0 % pass                                                                                                                                                          
    Password Store                                                                                                                                                                    
    ├── Library                                                                                                                                                                       
    │   └── Keychains                                                                                                                                                                 
    │       └── System.keychain                                                                                                                                                       
    │           └── Wifi                                                                                                                                                              
    │               ├── Airport-Express-Network                                                                                                                                       
    │               ├── EasyBox-123                                                                                                                                                
    │               └── TP-LINK_Extender_B123                                                                                                                                       
    └── Users                                                                                                                                                                         
        └── marko                                                                                                                                                                        
            └── Library                                                                                                                                                               
                └── Keychains                                                                                                                                                         
                    └── login.keychain                                                                                                                                             
                        ├── Apps                                                                                                                                                      
                        │   └── AIM
                        ├── Notes
                        │   └── my_notes
                        └── Websites
                            └── github.com

The encrypted files contains the password (for sure) and the username as well. The file has the following format.

    <password>
    login: <username>

This format can be used by the [chrome plugin](https://github.com/dannyvankooten/browserpass#readme).

You can copy the password via command line e.g.

    pass -c Users/marko/Library/Keychains/login.keychain/Websites/github.com

You can copy the username via command line e.g.

     pass Users/marko/Library/Keychains/login.keychain/Websites/github.com | grep login | awk '{print $2}' | pbcopy


### Tested With

This tool was tested with

    [Tue 18, 20:35] 0 % java -version                                                                                      
    java version "1.8.0_77"
    Java(TM) SE Runtime Environment (build 1.8.0_77-b03)
    Java HotSpot(TM) 64-Bit Server VM (build 25.77-b03, mixed mode)

    [Tue 18, 20:37] 1 % scala -version
    Scala code runner version 2.12.2 -- Copyright 2002-2017, LAMP/EPFL and Lightbend, Inc.

    [Tue 18, 20:37] 0 % sw_vers       
    ProductName:    Mac OS X
    ProductVersion: 10.11.6
    BuildVersion:   15G31
    
    