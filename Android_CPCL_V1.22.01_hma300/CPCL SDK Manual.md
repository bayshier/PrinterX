<center><bord>Android CPCL SDK Manual</bord></center>



[TOC]

------

### 1 **SDK Load AND USED**

1.1 Load the SDK jar package and so library of our printer in Android studio。

- Copy the jar package and so library to the app/libs folder

- Add the following code to build.gradle in app

  ```java
  android{
    sourceSets {
          main {
              jniLibs.srcDirs = ['libs']
          }
      }
  }
  implementation  files('libs/CPCL_SDK_V1.01.jar')
  ```



1.2 The interfaces we use are all static in the PrinterHelperclass and can be called directly.









------

### 2 **SDK Connection**

#### 	2.1 **Bluetooth Connection**

- Description

  ```java
  int portOpenBT(String portSetting)
  ```

- Parameter

  | Parameter   | Description                   |
  | ----------- | ----------------------------- |
  | portSetting | Bluetooth address (uppercase) |

- Return

  | value | Description                                           |
  | :---- | :---------------------------------------------------- |
  | 0     | connection success                                    |
  | -1    | connection time out                                   |
  | -2    | Bluetooth address format error                        |
  | -3    | printer mismatches with SDK (handshake command error) |

  

------

#### 	2.2 **WIFI Connection**

- Description

  ```java
  int portOpenWIFI(String printIP)
  ```

- Parameter

  | Parameter | Description |
  | :-------- | :---------- |
  | printIP   | IP address  |

- Return

  | value | Description                                           |
  | ----- | ----------------------------------------------------- |
  | 0     | connection success                                    |
  | -1    | connection time out                                   |
  | -2    | IP address format error                               |
  | -3    | printer mismatches with SDK (handshake command error) |

  

------

#### 	2.3 **USB Connection**

- Description

  ```java
  int portOpenUSB(Context context, UsbDevice usbdevice)
  ```

- Parameter

  | Parameter | Description                       |
  | :-------- | :-------------------------------- |
  | context   | Context object                    |
  | usbdevice | USB device object to be connected |

- Return

  | value | Description                                           |
  | ----- | ----------------------------------------------------- |
  | 0     | connection success                                    |
  | -1    | connection time out                                   |
  | -2    | paramete error                                        |
  | -3    | printer mismatches with SDK (handshake command error) |

  

------

#### 	2.4 **Disconnection**

- Description

  ```java
  boolean portClose()
  ```

- Parameter
  Null

- Return

  | value | Description           |
  | ----- | --------------------- |
  | true  | disconnection success |
  | false | disconnection failure |

  

------

### 3 **SDK Print Interface**

#### 	3.1 **Page label start**

- Description

  ```java
  int printAreaSize(String offset,String Horizontal, String Vertical,String height,String qty)
  ```

- Parameter

  | Parameter  | Description                                                  |
  | :--------- | :----------------------------------------------------------- |
  | offset     | Left margin                                                  |
  | Horizontal | Printer horizontal dpi (set according to actual printer dpi) |
  | Vertical   | Printer vertical dpi (set according to actual printer dpi)   |
  | height     | label height （unit：dot）200dpi  8 dot = 1mm，300dpi 12 dot = 1mm |
  | qty        | print number                                                 |

- Return

  | value | Description  |
  | ----- | ------------ |
  | >0    | send success |
  | -1    | send failure |

- Example

  ```java
  	PrinterHelper.printAreaSize("0","200","200","100","1")
  	PrinterHelper.Text(PrinterHelper.TEXT,"4","0","0","0","TEXT")
  	PrinterHelper.Form()//For positioning of printing label(Exclude continuous paper)
  	PrinterHelper.Print()
  ```

  

------

#### 	3.2 **Print**

- Description

  ```java
  int Print()
  ```

- Parameter
  Null

- Return

  | value | Description  |
  | ----- | ------------ |
  | >0    | send success |
  | -1    | send failure |

- Example

  ```java
  	PrinterHelper.printAreaSize("0","200","200","100","1")
  	PrinterHelper.Text(PrinterHelper.TEXT,"4","0","0","0","TEXT")
  	PrinterHelper.Form()//For positioning of printing label(Exclude continuous paper)
  	PrinterHelper.Print()
  ```

  

------

#### 	3.3 **Set Encoding**

- Description

  ```java
  int Encoding(String code)
  ```

- Parameter

  | Parameter | Description                                                  |
  | :-------- | :----------------------------------------------------------- |
  | code      | encoding，<br />gb2312： china ，<br />ISO8859-1 ：Western European<br />ISO8859-2 ：Latin (2)<br />ISO8859-3 ：Latin (3)<br />ISO8859-4 ：Baltic<br />ISO8859-5 ：Cyrillic<br />ISO8859-6 ：Arabic<br />ISO8859-8 ：Hebrew<br />ISO8859-9 ：Turkish<br />ISO8859-15 ：Latin（9）<br />iso8859-11 ：Greek（windows）<br />iso8859-7：Greek（ISO）<br />windows-874: Thai |

- Return

  | value | Description  |
  | ----- | ------------ |
  | >0    | send success |
  | -1    | send failure |

- Example

  ```java
  	PrinterHelper.printAreaSize("0","200","200","100","1")
  	PrinterHelper.Encoding(ISO8859-1)
  	PrinterHelper.Text(PrinterHelper.TEXT,"4","0","0","0","Simple Chinese")
  	PrinterHelper.Form()
  	PrinterHelper.Print()
  ```

  

------

#### 	3.4 **Label positioning**

- Description
  only effective before with PRINT, the command is only effective for the label

  ```java
  int Form()
  ```

- Parameter
  Null

- Return

  | value | Description  |
  | ----- | ------------ |
  | >0    | send success |
  | -1    | send failure |

- Example

  ```java
  	PrinterHelper.printAreaSize("0","200","200","100","1")
  	PrinterHelper.Text(PrinterHelper.TEXT,"4","0","0","0","TEXT")
  	PrinterHelper.Form()//For positioning of printing label(Exclude continuous paper)
  	PrinterHelper.Print()
  ```

  

------

#### 	3.5 **Note**

- Description

  ```java
  int Note(String note)
  ```

- Parameter

  | Parameter | Description   |
  | :-------- | :------------ |
  | note      | Notes content |

- Return

  | value | Description  |
  | ----- | ------------ |
  | >0    | send success |
  | -1    | send failure |

- Example

  ```java
  	PrinterHelper.printAreaSize("0","200","200","100","1")
  	PrinterHelper.Note("Note：")
  	PrinterHelper.Text(PrinterHelper.TEXT,"4","0","0","0","TEXT")
  	PrinterHelper.Form()
  	PrinterHelper.Print()
  ```

  

------

#### 	3.6 **Terminate**

- Description

  ```java
  int Abort()
  ```

- Parameter
  Null

- Return

  | value | Description  |
  | ----- | ------------ |
  | >0    | send success |
  | -1    | send failure |

  

------

#### 	3.7 **Text Print**

- Description

  **PrintTextCPCL** For Chinese firmware.

  **PrintCodepageTextCPCL**	 For English firmware.

  **Text**	Both firmware can be used.

  **printTextPro** Select the font library printing text.

  

  ```java
  int Text(String command,String font,String size ,String x,String y,String data)
  ```

- Parameter

  | Parameter | Description                                                  |
  | :-------- | :----------------------------------------------------------- |
  | command   | the direction of text <br />PrinterHelper.TEXT：horizontal<br/>PrinterHelper.TEXT90：rotate 90° CCW<br/>PrinterHelper.TEXT180：rotate 180° CCW<br/>PrinterHelper.TEXT270：rotate 270° CCW |
  | font      | size of font dot matrix:（unit：dot）<br />Note: English firmware only supports (0 and 1).<br/>0：12x24<br/>1：12x24(print traditional Chinese)The font in English mode becomes (9x17)<br/>2：8x16<br/>3：20x20<br/>4：32x32 or 16x32, magnifies the width and height of ID3 font by 2 times<br/>7：24x24 or 12x24, depends on Chinese and English<br/>8：24x24 or 12x24, depends on Chinese and English<br/>20：16x16 or 8x16, depends on Chinese and English<br/>24：24x24 or 12x24, depends on Chinese and English<br/>55：16x16 or 8x16, depends on Chinese and English<br/>Others default 24x24 or 12x24,depends on Chinese and English |
  | size      | size of font (This function is being blocked, parameter transmits 0) |
  | x         | x-coordinate（unit dot）                                     |
  | y         | x-coordinate（unit dot）                                     |
  | data      | text data                                                    |

- Return

  | value | Description  |
  | ----- | ------------ |
  | >0    | send success |
  | -1    | send failure |

- Example

  ```java
  	PrinterHelper.printAreaSize("0","200","200","500","1")
  	PrinterHelper.SetBold("1")//Make fonts bold (if unnecessary, no need to 	add)
  	PrinterHelper.SetMag("2","2")//Make fonts magnified (if unnecessary, no 	need to add)
  	PrinterHelper.Text(PrinterHelper.TEXT,"7","0","10","10","TEXT")
  	PrinterHelper.SetMag("1","1")//Turn off magnification
  	PrinterHelper.SetBold("0")//Turn off bold
  	PrinterHelper.Form()
  	PrinterHelper.Print()
  ```

  

------

- 

  ```java
  int PrintTextCPCL(String command,int font ,String x,String y,String data,int n,boolean 	Iscenter,int width)
  ```

- Parameter

  | Parameter | Description                                                  |
  | :-------- | :----------------------------------------------------------- |
  | command   | the direction of text <br />PrinterHelper.TEXT：horizontal<br/>PrinterHelper.TEXT270：vertical |
  | font      | size of font dot matrix：（unit：dot）<br />1：print traditional Chinese font (24x24 or 12x24, depends on Chinese and English)<br/>16：16x16 or 8x16, depends on Chinese and English<br/>24：24x24 or 12x24, depends on Chinese and English<br/>32：32x32 or 16x32, magnifies the width and height of ID3 font by 2 times |
  | x         | x-coordinate（unit dot）                                     |
  | y         | y-coordinate（unit dot）                                     |
  | data      | text data                                                    |
  | n         | special effect of font：<br />N&1==1：Bold<br/>N&2==2：Inverse<br/>N&4==4：Double width<br/>N&8==8：Double height |
  | Iscenter  | whether center<br />true： yes<br/>false：no                 |
  | Width     | the centering range (Effective when Iscenter=true ) unit：dot |

- Return

  | value | Description  |
  | ----- | ------------ |
  | >0    | send success |
  | -1    | send failure |

- Example

  ```java
  	PrinterHelper.printAreaSize("0","200","200","500","1")
  	//15 indicates with all the special effects
  	PrinterHelper.PrintTextCPCL(PrinterHelper.TEXT,24,"10","10","TEXT",15,false,0)
  	PrinterHelper.Form()
  	PrinterHelper.Print()
  ```

  

------

- 

  ```java
  int PrintCodepageTextCPCL(String command,int font ,String x,String y,String data,int n)
  ```

- Parameter

  | Parameter | Description                                                  |
  | :-------- | :----------------------------------------------------------- |
  | command   | the direction of text <br />PrinterHelper.TEXT：horizontal<br/>PrinterHelper.TEXT270：vertical |
  | font      | size of font dot matrix：（unit：dot）<br />0：12x24。<br/>1：9x17。 |
  | x         | x-coordinate（unit dot）                                     |
  | y         | y-coordinate（unit dot）                                     |
  | data      | text data                                                    |
  | n         | special effect of font：<br />N&1==1：Bold<br/>N&2==2：Inverse<br/>N&4==4：Double width<br/>N&8==8：Double height |

- Return

  | value | Description  |
  | ----- | ------------ |
  | >0    | send success |
  | -1    | send failure |

- Example

  ```java
  	PrinterHelper.printAreaSize("0","200","200","500","1")
  	PrinterHelper.Country("ISO8859-1");//set printer codepage
  	PrinterHelper.LanguageEncode="iso8859-1";//set SDK codepage
  	//15indicates with all the special effects
  	PrinterHelper.PrintCodepageTextCPCL(PrinterHelper.TEXT,0,"10","10","TEXT",15)
  	PrinterHelper.Form()
  	PrinterHelper.Print()
  ```

  

------

- Select the font library printing text

- Describe：

  The interface selects the corresponding font printer through the name of the font library, and the font needs to download it to the printer first through the tool.

  ```java
  int printTextPro(String command, String fontName, int xScale, int yScale, int x, int y, String data)
  ```

- Parameter

  | Parameter | Description                                                  |
  | :-------- | :----------------------------------------------------------- |
  | command   | the direction of text <br />PrinterHelper.TEXT：horizontal<br/>PrinterHelper.TEXT90：rotate 90° CCW<br/>PrinterHelper.TEXT180：rotate 180° CCW<br/>PrinterHelper.TEXT270：rotate 270° CCW |
  | fontName  | Font name：<br />SIMSUN.TTF<br />TT0003M_.TTF                |
  | xScale    | X -axis direction font amplification multiple                |
  | yScale    | Y -axis direction font amplification multiple                |
  | x         | x-coordinate（unit dot）                                     |
  | y         | y-coordinate（unit dot）                                     |
  | data      | text data                                                    |

- Return

  | value | Description  |
  | ----- | ------------ |
  | >0    | send success |
  | -1    | send failure |

- Example

  ```java
  	PrinterHelper.printAreaSize("0","200","200","500","1")
  	PrinterHelper.Country("ISO8859-1");//set printer codepage
  	PrinterHelper.LanguageEncode="iso8859-1";//set SDK codepage
  	PrinterHelper.printTextPro(PrinterHelper.TEXT,"SIMSUN.TTF",1,1,0,0,"Test")
  	PrinterHelper.Form()
  	PrinterHelper.Print()
  ```

  

------



#### 	3.8 **Count**

- Description

  ```java
  int Count(String ml)
  ```

- Parameter

  | Parameter | Description               |
  | :-------- | :------------------------ |
  | note      | next added or minus value |

- Return

  | value | Description  |
  | ----- | ------------ |
  | >0    | send success |
  | -1    | send failure |

- Example

  ```java
  	PrinterHelper.printAreaSize("0","200","200","100","2")
  	PrinterHelper.Text(PrinterHelper.TEXT,"4","0","0","0","10086")
  	PrinterHelper.Count("10")
  	PrinterHelper.Text(PrinterHelper.TEXT,"4","0","0","0","10000")
  	PrinterHelper.Count("-10")
  	PrinterHelper.Form()
  	PrinterHelper.Print()
  ```

  

------

#### 	3.9 **Set magnification times of character width and height**

- Description

  ```java
  int SetMag(String width,String height)
  ```

- Parameter

  | Parameter | Description                   |
  | :-------- | :---------------------------- |
  | width     | magnification times of width  |
  | height    | magnification times of height |

- Return

  | value | Description  |
  | ----- | ------------ |
  | >0    | send success |
  | -1    | send failure |

- Example

  ```java
  	PrinterHelper.printAreaSize("0","200","200","500","1")
    //Make the following fonts bold (if unnecessary, no need to 	add)
  	PrinterHelper.SetBold("1")
    //Make the following fonts magnified (if unnecessary, no need to add)
  	PrinterHelper.SetMag("2","2")
  	PrinterHelper.Text(PrinterHelper.TEXT,"7","0","10","10","TEXT")
  	PrinterHelper.SetMag("1","1")//Turn off magnification
  	PrinterHelper.SetBold("0")//Turn off bold
  	PrinterHelper.Form()
	PrinterHelper.Print()
  ```
  
  

------

#### 	3.10 **Alignment**

- Description

  ```java
  int Align(String align)
  ```

- Parameter

  | Parameter | Description                                                  |
  | :-------- | :----------------------------------------------------------- |
  | align     | PrinterHelper.CENTER：center<br/>PrinterHelper.LEFT：left alignment<br/>PrinterHelper.RIGHT：right alignment |

- Return

  | value | Description  |
  | ----- | ------------ |
  | >0    | send success |
  | -1    | send failure |

- Example

  ```java
  	PrinterHelper.printAreaSize("0","200","200","100","1")
  	PrinterHelper.Align(PrinterHelper.CENTER)
  	PrinterHelper.Text(PrinterHelper.TEXT,"4","0","0","0","TEXT")
  	PrinterHelper.Form()
  	PrinterHelper.Print()
  ```

  

------

#### 	3.11 **Print Barcode**

- Description

  ```java
  int Barcode(String command,String type,String width, String ratio,String height,String 			x,String y,boolean undertext,String number,String size,String offset, String data )
  ```

- Parameter

  | Parameter | Description                                                  |
  | :-------- | :----------------------------------------------------------- |
  | command   | PrinterHelper.BARCODE：horizontal<br/>PrinterHelper.VBARCODE：vertical |
  | type      | type of bar code：<br/>PrinterHelper.UPCA,PrinterHelper.UPCA2,PrinterHelper.UPCA5,<br />PrinterHelper.UPCE, PrinterHelper.UPCE2,PrinterHelper.UPCE5 ,<br/>PrinterHelper.EAN13,PrinterHelper.EAN132,PrinterHelper.EAN135,<br />PrinterHelper.EAN8, PrinterHelper.EAN82,PrinterHelper.EAN85,<br/>PrinterHelper.code39, PrinterHelper.code39C,PrinterHelper.F39,<br />PrinterHelper.F39C,PrinterHelper.code93,PrinterHelper.I2OF5,<br/>PrinterHelper.I2OF5C,PrinterHelper.I2OF5G,PrinterHelper.code128,<br />PrinterHelper.UCCEAN128,PrinterHelper.CODABAR,PrinterHelper.CODABAR16,<br/>PrinterHelper.MSI,PrinterHelper.MSI10,PrinterHelper.MSI1010,<br />PrinterHelper.MSI1110,PrinterHelper.POSTNET,PrinterHelper.FIM |
  | width     | the unit width of narrow bar                                 |
  | ratio     | the ratio of wide bar and narrow bar, as below<br />0=1.5:1 ,  1=2.0:1 ,  2=2.5:1 ,  3=3.0:1 ,  4=3.5:1 ,<br/>20=2.0:1 , 21=2.1:1 , 22=2.2:1 , 23=2.3:1 , 24=2.4:1 , 25=2.5:1<br/>26=2.6:1 , 27=2.7:1 , 28=2.8:1 , 29=2.9:1 , 30=3.0:1 , |
  | height    | height of bar code                                           |
  | x         | start x-coordinate of bar code（unit：dot）                  |
  | y         | start y-coordinate of bar code（unit：dot）                  |
  | undertext | whether the data below bar code is visible.<br />ture：visible，false：invisible |
  | number    | type of font (undertext=true just take effect)               |
  | size      | size of font(undertext=true just take effect)                |
  | offset    | distance between bar code and text(undertext=true just take effect) |
  | data      | data of bar code                                             |

- Return

  | value | Description  |
  | ----- | ------------ |
  | >0    | send success |
  | -1    | send failure |

- Example

  ```java
  	PrinterHelper.printAreaSize("0","200","200","100","1")
  	PrinterHelper.Barcode(PrinterHelper.BARCODE,PrinterHelper.128,"1","1","50","0","0",
  	true,"7","0","5","123456789")
  	PrinterHelper.Form()
  	PrinterHelper.Print()
  ```

  

------

#### 3.12 **Print QRcode**

- Description

  ```java
  int PrintQR(String command, String x, String y, String M , String U, String data )
  ```

- Parameter

  | Parameter | Description                                                  |
  | :-------- | :----------------------------------------------------------- |
  | command   | PrinterHelper.BARCODE：horizontal<br/>PrinterHelper.VBARCODE：vertical |
  | x         | start x-coordinate of QR code（unit：dot）                   |
  | y         | start y-coordinate of QR code（unit：dot）                   |
  | M         | Type of QR：<br />1：Common type<br />2：Added individual symbols on the basis of Type 1 |
  | U         | unit width/unit height of module.Range is from 1 to 32, default is 6. |
  | data      | data of QR code                                              |

- Return

  | value | Description  |
  | ----- | ------------ |
  | >0    | send success |
  | -1    | send failure |

- Example

  ```java
  	PrinterHelper.printAreaSize("0","200","200","100","1")
  	PrinterHelper.PrintQR(PrinterHelper.BARCODE, "0", "0", "2" , "6", "123ABC" )
  	PrinterHelper.Form()
  	PrinterHelper.Print()
  ```

  

------

#### 3.13 **Print PDF417code**

- Description

  ```java
  int PrintPDF417(String command, String x, String y, String XD , String YD, String C, 											String S , String data)
  ```

- Parameter

  | Parameter | Description                                                  |
  | :-------- | :----------------------------------------------------------- |
  | command   | PrinterHelper.BARCODE：horizontal<br/>PrinterHelper.VBARCODE：vertical |
  | x         | start x-coordinate of PDF417 code（unit：dot）               |
  | y         | start y-coordinate of PDF417（unit：dot）                    |
  | XD        | unit width of narrowest element, range is from 1 to 32, default is 2. |
  | YD        | unit height of narrowest element, range is from 1 to 32, default is 6. |
  | C         | number of column that used, data column excludes start/stop character and left/right index. Range is from 1 to 30, default is 3. |
  | S         | Security level indicates the maximum value and/or calibration of error to detect. 	Range is from 0 to 8, default is 1. |
  | data      | data of PDF417 code                                          |

- Return

  | value | Description  |
  | ----- | ------------ |
  | >0    | send success |
  | -1    | send failure |

- Example

  ```java
  	PrinterHelper.printAreaSize("0","200","200","100","1")
  	PrinterHelper.PrintPDF417(PrinterHelper.BARCODE,"0","0","2","6","3","1","123ABC")
  	PrinterHelper.Form()
  	PrinterHelper.Print()
  ```

  

------

#### 3.14 **Print rectangular frame**

- Description

  ```java
  int Box(String X0,String Y0,String X1,String Y1,String width)
  ```

- Parameter

  | Parameter | Description                                   |
  | :-------- | :-------------------------------------------- |
  | X0        | x-coordinate of top left corner（unit：dot）  |
  | Y0        | y-coordinate of top left corner（unit：dot）  |
  | X1        | x-coordinate of top right corner（unit：dot） |
  | Y1        | y-coordinate of top right corner（unit：dot） |
  | width     | unit width of the line (default：1)           |

- Return

  | value | Description  |
  | ----- | ------------ |
  | >0    | send success |
  | -1    | send failure |

- Example

  ```java
  	PrinterHelper.printAreaSize("0","200","200","200","1")
  	PrinterHelper.Box("0","0","150","150","1")
  	PrinterHelper.Form()
  	PrinterHelper.Print()
  ```

  

------

#### 3.15 **Print Line**

- Description

  ```java
  int Line(String X0,String Y0,String X1,String Y1,String width )
  ```

- Parameter

  | Parameter | Description                         |
  | :-------- | :---------------------------------- |
  | X0        | start x-coordinate（unit：dot）     |
  | Y0        | start y-coordinate（unit：dot）     |
  | X1        | end x-coordinate（unit：dot）       |
  | Y1        | end y-coordinate（unit：dot）       |
  | width     | unit width of the line (default：1) |

- Return

  | value | Description  |
  | ----- | ------------ |
  | >0    | send success |
  | -1    | send failure |

- Example

  ```java
  	PrinterHelper.printAreaSize("0","200","200","200","1")
  	PrinterHelper.Line("10","10","150","10","1")
  	PrinterHelper.Form()
  	PrinterHelper.Print()
  ```

  

------

#### 3.16 **Inverse Line**

- Description

  ```java
  int InverseLine(String X0,String Y0,String X1,String Y1,String width )
  ```

- Parameter

  | Parameter | Description                          |
  | :-------- | :----------------------------------- |
  | X0        | start x-coordinate（unit：dot）      |
  | Y0        | start y-coordinate（unit：dot）      |
  | X1        | end x-coordinate（unit：dot）        |
  | Y1        | end y-coordinate（unit：dot）        |
  | width     | unit width of the line  (default：1) |

- Return

  | value | Description  |
  | ----- | ------------ |
  | >0    | send success |
  | -1    | send failure |

- Example

  ```java
  	PrinterHelper.printAreaSize("0","200","200","200","1")
  	PrinterHelper.InverseLine("10","10","150","10","1")
  	PrinterHelper.Form()
  	PrinterHelper.Print()
  ```

  

------

#### 3.17 **Print Image**

- Description（1）

  ```java
  int printBitmapCPCL(Bitmap bitmap,int x,int y,int type,int compressType,int light)
  ```

- Parameter

  | Parameter    | Description                                                  |
  | :----------- | :----------------------------------------------------------- |
  | bitmap       | The Bitmap object of the picture to be printed (you need to adjust the picture size by yourself, 200dpi 8px=1mm) |
  | x            | start x-coordinate of image（unit：dot）                     |
  | y            | start y-coordinate of image（unit：dot）                     |
  | type         | type of printing image<br />0：black and white<br />1：halftone<br />2：Aggregation |
  | compressType | 0：No compression，<br />1：Overall compression，（Suitable for smaller pictures）<br />2：Subpackage compression，（Suitable for larger pictures） |
  | light        | Brightness (range -100 to 100)                               |

- Return

  | value | Description                             |
  | ----- | --------------------------------------- |
  | >0    | send success                            |
  | -1    | send failure                            |
  | -2    | Bitmap null                             |
  | -3    | Picture data exceeds the printer buffer |

- Example

  ```java
  	PrinterHelper.printAreaSize("0","200","200","500","1")
  	PrinterHelper.printBitmapCPCL(bitmap,0,0,0,0,0)
  	PrinterHelper.Form()
  	PrinterHelper.Print()
  ```

- Description（2）
     This interface can only be used in line mode (it already contains the beginning interface and the Print interface).

  ```java
  int printBitmap(int x,int y,int type,Bitmap bitmap,int compressType,boolean isform,int segments)
  ```

- Parameter

  | Parameter    | Description                                                  |
  | :----------- | :----------------------------------------------------------- |
  | bitmap       | The Bitmap object of the picture to be printed (you need to adjust the picture size by yourself, 200dpi 8px=1mm) |
  | x            | start x-coordinate of image（unit：dot）                     |
  | y            | start y-coordinate of image（unit：dot）                     |
  | type         | type of printing image<br />0：black and white<br />1：halftone<br />2：Aggregation |
  | compressType | 0：No compression，<br />1：Overall compression，（Suitable for smaller pictures）<br />2：Subpackage compression，（Suitable for larger pictures） |
  | Isform       | Whether to locate, (only use false in continuous paper mode) |
  | segments     | Subcontract times (not less than 1) default 1                |

- Return

  | value | Description                             |
  | ----- | --------------------------------------- |
  | >0    | send success                            |
  | -1    | send failure                            |
  | -2    | Bitmap null                             |
  | -3    | Picture data exceeds the printer buffer |

- Example

  ```java
  PrinterHelper.printBitmap(0,0,0,bitmap,0,false,1)
  ```




- Description（3）

  ```java
  int printBitmapBase64(String base64, int x, int y, int type, int compressType, int light)
  ```

- Parameter

  | Parameter    | Description                                                  |
  | :----------- | :----------------------------------------------------------- |
  | base64       | base64 string of the image file                              |
  | x            | start x-coordinate of image（unit：dot）                     |
  | y            | start y-coordinate of image（unit：dot）                     |
  | type         | type of printing image<br />0：black and white<br />1：halftone<br />2：Aggregation |
  | compressType | 0：No compression，<br />1：Overall compression，（Suitable for smaller pictures）<br />2：Subpackage compression，（Suitable for larger pictures） |
  | light        | Brightness (range -100 to 100)                               |

- Return

  | value | Description                             |
  | ----- | --------------------------------------- |
  | >0    | send success                            |
  | -1    | send failure                            |
  | -2    | base64 null                             |
  | -3    | Picture data exceeds the printer buffer |

- Example

  ```java
  	PrinterHelper.printAreaSize("0","200","200","500","1")
  	PrinterHelper.printBitmapBase64(base64,0,0,0,0,0)
  	PrinterHelper.Form()
  	PrinterHelper.Print()
  ```

  

------

#### 3.18 **Print Density**

- Description

  ```java
  int Contrast(String contrast)
  ```

- Parameter

  | Parameter | Description                                                  |
  | :-------- | :----------------------------------------------------------- |
  | contrast  | type of density, totally 4 kinds：<br/>Default = 0<br/>Medium = 1<br/>Dark = 2<br/>Very Dark = 3 |

- Return

  | value | Description  |
  | ----- | ------------ |
  | >0    | send success |
  | -1    | send failure |

- Example

  ```java
  PrinterHelper.printAreaSize("0","200","200","100","1")
  PrinterHelper.Contrast("1")
  PrinterHelper.Text(PrinterHelper.TEXT,"4","0","0","0","TEXT")
  PrinterHelper.Form()
  PrinterHelper.Print()
  ```

  

------

#### 3.19 **Print Speed**

- Description

  ```java
  int Speed(String speed )
  ```

- Parameter

  | Parameter | Description                                                  |
  | :-------- | :----------------------------------------------------------- |
  | speed     | 5 types: from 0 to 5, the speed is increasing; 5 is the fastest speed in ideal state. |

- Return

  | value | Description  |
  | ----- | ------------ |
  | >0    | send success |
  | -1    | send failure |

- Example

  ```java
  PrinterHelper.printAreaSize("0","200","200","100","1")
  PrinterHelper.Speed("4")
  PrinterHelper.Text(PrinterHelper.TEXT,"4","0","0","0","TEXT")
  PrinterHelper.Form()
  PrinterHelper.Print()
  ```

  

------

#### 3.20 **Set Spacing**

- Description

  ```java
  int SetSp( String setsp)
  ```

- Parameter

  | Parameter | Description          |
  | :-------- | :------------------- |
  | Setsp     | spacing（unit：row） |

- Return

  | value | Description  |
  | ----- | ------------ |
  | >0    | send success |
  | -1    | send failure |

- Example

  ```java
  PrinterHelper.printAreaSize("0","200","200","100","1")
  PrinterHelper.Text(PrinterHelper.TEXT,"4","0","0","0","TEXT")
  PrinterHelper.SetSp(1)
  PrinterHelper.Text(PrinterHelper.TEXT,"4","0","0","50","TEXT")
  PrinterHelper.SetSp(5)
  PrinterHelper.Text(PrinterHelper.TEXT,"4","0","0","90","TEXT")
  PrinterHelper.Form()
  PrinterHelper.Print()
  ```

  

------

#### 3.21 **Paper Feed**

- Description

  ```java
  int Prefeed( String prefeed)
  ```

- Parameter

  | Parameter | Description                             |
  | :-------- | :-------------------------------------- |
  | prefeed   | the distance of paper feed（unit：dot） |

- Return

  | value | Description  |
  | ----- | ------------ |
  | >0    | send success |
  | -1    | send failure |

- Example

  ```java
  PrinterHelper.printAreaSize("0","200","200","100","1")
  PrinterHelper.Prefeed("40")
  PrinterHelper.Text(PrinterHelper.TEXT,"4","0","0","0","TEXT")
  PrinterHelper.Form()
  PrinterHelper.Print()
  ```

  

------

#### 3.22 **Paper Feed Post Printing**

- Description

  ```java
  int Postfeed( String posfeed)
  ```

- Parameter

  | Parameter | Description                             |
  | :-------- | :-------------------------------------- |
  | posfeed   | the distance of paper feed（unit：dot） |

- Return

  | value | Description  |
  | ----- | ------------ |
  | >0    | send success |
  | -1    | send failure |

- Example

  ```java
  PrinterHelper.printAreaSize("0","200","200","100","1")
  PrinterHelper.Text(PrinterHelper.TEXT,"4","0","0","0","TEXT")
  PrinterHelper.Form()
  //Note：It should be after FORM command
  PrinterHelper.Postfeed("40")
  PrinterHelper.Print()
  ```

  

------

#### 3.23 **Beeper**

- Description

  ```java
  int Beep( String beep)
  ```

- Parameter

  | Parameter | Description                                      |
  | :-------- | :----------------------------------------------- |
  | beep      | the lasting time of beeper，（1/8) unit : second |

- Return

  | value | Description  |
  | ----- | ------------ |
  | >0    | send success |
  | -1    | send failure |

- Example

  ```java
  PrinterHelper.printAreaSize("0","200","200","100","1")
  PrinterHelper.Text(PrinterHelper.TEXT,"4","0","0","0","TEXT")
  PrinterHelper.Beep("16")
  PrinterHelper.Form()
  PrinterHelper.Print()
  ```

  

------

#### 3.24 **Underline**

- Description

  ```java
  int Underline(boolean UL)
  ```

- Parameter

  | Parameter | Description                                        |
  | :-------- | :------------------------------------------------- |
  | UL        | true：add underline，<br />false：cancel underline |

- Return

  | value | Description  |
  | ----- | ------------ |
  | >0    | send success |
  | -1    | send failure |

- Example

  ```java
  PrinterHelper.printAreaSize("0","200","200","100","1")
  PrinterHelper.Underline(true)
  PrinterHelper.Text(PrinterHelper.TEXT,"4","0","0","0","TEXT")
  PrinterHelper.Underline(false)
  PrinterHelper.Form()
  PrinterHelper.Print()
  ```

  

------

#### 3.25 **Wait Print**

- Description

  ```java
  int Wait( String wait)
  ```

- Parameter

  | Parameter | Description           |
  | :-------- | :-------------------- |
  | wait      | Wait unit：1/8 second |

- Return

  | value | Description  |
  | ----- | ------------ |
  | >0    | send success |
  | -1    | send failure |

- Example

  ```java
  PrinterHelper.printAreaSize("0","200","200","100","1")
  PrinterHelper.Wait("80")
  PrinterHelper.Text(PrinterHelper.TEXT,"4","0","0","0","TEXT")
  PrinterHelper.Form()
  PrinterHelper.Print()
  ```

  

------

#### 3.26 **Print Width**

- Description

  ```java
  int PageWidth(String pw)
  ```

- Parameter

  | Parameter | Description                     |
  | :-------- | :------------------------------ |
  | pw        | specify page width（unit：dot） |

- Return

  | value | Description  |
  | ----- | ------------ |
  | >0    | send success |
  | -1    | send failure |

- Example

  ```java
  PrinterHelper.printAreaSize("0","200","200","100","1")
  PrinterHelper.PageWidth("100")
  PrinterHelper.Text(PrinterHelper.TEXT,"4","0","0","0","TEXT")
  PrinterHelper.Form()
  PrinterHelper.Print()
  ```

  

------

#### 3.27 **Set Spacing Line Mode**

- Description

  ```java
  int Setlf(String SF)
  ```

- Parameter

  | Parameter | Description           |
  | :-------- | :-------------------- |
  | SF        | spacing（unit：line） |

- Return

  | value | Description  |
  | ----- | ------------ |
  | >0    | send success |
  | -1    | send failure |

- Example

  ```java
  PrinterHelper.PrintData("text1 to print\r\n");
  PrinterHelper.Setlf("5");
  PrinterHelper.PrintData("text2 to print\r\n");
  ```

  

------

#### 3.28 **Set Font Size Line Height in Line Mode**

- Description

  ```java
  int Setlp(String font,String size,String spacing )
  ```

- Parameter

  | Parameter | Description                                                  |
  | :-------- | :----------------------------------------------------------- |
  | font      | font size：（unit：dot）<br />Note:English firmware only supports (0 and 1)<br/>0：12x24。<br/>1：12x24 (print traditional Chinese) English mode becomes (9x17)<br/>2：8x16。<br/>3：20x20。<br/>4：32x32 or 16x32, magnifies the width and height of ID3 font by 2 times<br/>7：24x24 or 12x24, depends on Chinese and English<br/>8：24x24 or 12x24, depends on Chinese and English<br/>20：16x16 or 8x16, depends on Chinese and English<br/>24：24x24 or 12x24, depends on Chinese and English<br/>55：16x16 or 8x16, depends on Chinese and English<br/>Others default 24x24 or 12x24,depends on Chinese and English |
  | size      | size of font (This function is being blocked, parameter transmits 0) |
  | spacing   | Line height（unit：dot）                                     |

- Return

  | value | Description  |
  | ----- | ------------ |
  | >0    | send success |
  | -1    | send failure |

- Example

  ```java
  PrinterHelper.Setlp("5","2","32");
  PrinterHelper.PrintData("text to print\r\n");
  ```

  

------

#### 3.29 **Write Data**

- Description

  ```java
  int WriteData(byte[] bData)
  ```

- Parameter

  | Parameter | Description                               |
  | :-------- | :---------------------------------------- |
  | bData     | data that requires to send to the printer |

- Return

  | value | Description  |
  | ----- | ------------ |
  | >0    | send success |
  | -1    | send failure |

- Example

  ```java
  PrinterHelper.WriteData(new byte[]{0x0d,0x0a});
  ```

  

------

#### 3.30 **Read Data**

- Description

  ```java
  byte[] ReadData(int second)
  ```

- Parameter

  | Parameter | Description     |
  | :-------- | :-------------- |
  | second    | timeout(second) |

- Return

  | value                  | Description  |
  | ---------------------- | ------------ |
  | Data length 0 or empty | read failure |
  | >0                     | read succeed |

- Example

  ```java
  PrinterHelper.ReadData(2);
  ```

  

------

#### 3.31 **Set bold**

- Description

  ```java
  int SetBold(String bold)
  ```

- Parameter

  | Parameter | Description                 |
  | :-------- | :-------------------------- |
  | bold      | value of bold ((range：1-5) |

- Return

  | value | Description  |
  | ----- | ------------ |
  | >0    | send success |
  | -1    | send failure |

- Example

  ```java
  PrinterHelper.printAreaSize("0","200","200","500","1")
    //Make the following fonts bold (if unnecessary, no need to add)
  PrinterHelper.SetBold("1")
    //Make the following fonts magnified (if unnecessary, no need to add)
  PrinterHelper.SetMag("2","2")
  PrinterHelper.Text(PrinterHelper.TEXT,"7","0","10","10","TEXT")
  PrinterHelper.SetMag("1","1")//Turn off magnification
  PrinterHelper.SetBold("0")//Turn off bold 
  PrinterHelper.Form()
PrinterHelper.Print()
  ```
  
  

------

#### 3.32 **Get the printer status**

- Description

  ```java
  int getPrinterStatus()
  ```

- Parameter

  Null

- Return

  | value             | Description             |
  | ----------------- | ----------------------- |
  | status == 0       | printer is ready        |
  | status == -1      | send failure            |
  | (status & 2) == 2 | printer is out of paper |
  | (status & 4) == 4 | printer cover is open   |

- Example

  ```java
  int status = PrinterHelper.getPrinterStatus()//This interface is not a real-time command. When the printer is printing, the query is invalid
    if (status == 0){
    //printer is ready
    }
    if((status & 2) == 2){
      //printer is out of paper
    }
    if ((status & 4) == 4){
   		//printer cover is open
    }
  ```
  
  

------

#### 3.33 **Text line wrap**

- Description

  The two interfaces for this function are AutLine and AutLine2. The former cannot use Thai. The latter printer firmware must be above A300 V1.01.40.01 and the number of text bytes must not exceed 1024. The excess is automatically ignored.

  ```java
  int AutLine(String x,String y,int width,int size,boolean isbole,
                boolean isdouble,String str)
  ```

- Parameter

  | Parameter | Description                                                  |
  | :-------- | :----------------------------------------------------------- |
  | x         | start x-coordinate of text（unit：dot）                      |
  | y         | start y-coordinate of text（unit：dot）                      |
  | width     | print width of one line（unit：dot）                         |
  | size      | size of font<br />3：20x20 or 10x20, depends on Chinese and English<br/>4：32x32 or 16x32, magnifies the width and height of ID3 font by 2 times<br/>8：24x24 or 12x24, depends on Chinese and English<br/>55：16x16 or 8x16, depends on Chinese and English |
  | isbole    | true：bold<br/>false：not bold                               |
  | isdouble  | double the font<br />true：magnify<br/>false：not magnify    |
  | str       | text to print                                                |

- Return

  | value | Description  |
  | ----- | ------------ |
  | >0    | send success |
  | -1    | send failure |

- Example

  ```java
  PrinterHelper.printAreaSize("0","200","200","100","1")
  PrinterHelper.AutLine("0","0",100,4,true,true"Text")
  PrinterHelper.Form()
  PrinterHelper.Print()
  ```


- 

  ```java
  int AutLine2(String x,String y,int width,int size,boolean isbole,boolean isdouble,
                 String str)
  ```

- Parameter

  | Parameter | Description                                                  |
  | :-------- | :----------------------------------------------------------- |
  | x         | start x-coordinate of text（unit：dot）                      |
  | y         | start y-coordinate of text（unit：dot）                      |
  | width     | print width of one line（unit：dot）                         |
  | size      | size of font<br />0：24x24 or 12x24, depends on Chinese and English. (Thai: 24x48)<br/>1：7x19 (English), 24x24 (Traditional)<br/>3：20x20 or 10x20, depends on Chinese and English<br/>4：32x32 or 16x32, magnifies the width and height of ID3 font by 2 times<br/>8：24x24 or 12x24, depends on Chinese and English<br/>55：16x16 or 8x16, depends on Chinese and English |
  | isbole    | true：bold<br/>false：not bold                               |
  | isdouble  | double the font<br />true：magnify<br/>false：not magnify    |
  | str       | text to print                                                |

- Return

  | value | Description  |
  | ----- | ------------ |
  | >0    | send success |
  | -1    | send failure |

- Example

  ```java
  PrinterHelper.printAreaSize("0","200","200","100","1")
  PrinterHelper.AutLine2("0","0",100,4,true,true"Text")
  PrinterHelper.Form()
  PrinterHelper.Print()
  ```

  

------

#### 3.34 **Text showed center in the textbox**

- Description

  ```java
  int AutCenter(String command, String x,String y,int width,int size,String str)
  ```

- Parameter

  | Parameter | Description                                                  |
  | :-------- | :----------------------------------------------------------- |
  | command   | the direction of text, totally 2 kinds：<br/>PrinterHelper.TEXT：horizontal<br/>PrinterHelper.TEXT270：vertical |
  | x         | start x-coordinate of textbox（unit：dot）                   |
  | y         | start y-coordinate of textbox（unit：dot）                   |
  | width     | width of textbox（unit：dot）                                |
  | size      | size of font<br/>3：16x16 or 8x16, depends on Chinese and English<br/>4：32x32 or 16x32, magnifies the width and height of ID3 font by 2 times<br/>8：24x24 or 12x24, depends on Chinese and English<br/>55：16x16 or 8x16, depends on Chinese and English |
  | str       | text to print                                                |

- Return

  | value | Description  |
  | ----- | ------------ |
  | >0    | send success |
  | -1    | send failure |

- Example

  ```java
  PrinterHelper.printAreaSize("0","200","200","100","1")
  PrinterHelper.AutCenter(PrinterHelper.TEXT,"0","0",100,4,"Text")
  PrinterHelper.Form()
  PrinterHelper.Print()
  ```

  

------

#### 3.35 **Set paper type of printer**

- Description

  ```java
  void papertype_CPCL(int page)
  ```

- Parameter

  | Parameter | Description                                                  |
  | :-------- | :----------------------------------------------------------- |
  | page      | type of paper<br/>0：continuous paper<br/>1：label paper<br/>2：back black mark<br/>3：front black mark<br/>4：3 inch black mark<br/>5：2 inch black mark |

- Return
  Null

- Example

  ```java
  //Only applies to A300.
  PrinterHelper.papertype_CPCL(0)//set as continuous paper
  ```

  

------

#### 3.36 **Self-test page**

- Description

  ```java
  void setSelf()
  ```

- Parameter

  Null

- Return
  Null

- Example

  ```java
  PrinterHelper.setSelf()//Pinter will print some of its parameter
  ```

  

------

#### 3.37 **Rotate 180° to print**

- Description

  ```java
  int PoPrint()
  ```

- Parameter

  Null

- Return

  | value | Description  |
  | ----- | ------------ |
  | >0    | send success |
  | -1    | send failure |

- Example

  ```java
  PrinterHelper.printAreaSize("0","200","200","100","1")
  PrinterHelper.Text(PrinterHelper.TEXT,"4","0","0","0","TEXT")
  PrinterHelper.Form()//For positioning of printing label(Exclude continuous paper)
  PrinterHelper.PoPrint()
  ```

  

------

#### 3.38 **ON/OFF getting the status when print completed**

- Description

  It requires to use with getEndStatus(), and remember to turn off after use (printer firmware version should be above A300 V1.01.27.01).

  ```java
  void openEndStatic(boolean isopen)
  ```

- Parameter

  | Parameter | Description                 |
  | :-------- | :-------------------------- |
  | isopen    | true：open<br/>false：close |

- Return
  Null

- Example

  ```java
  PrinterHelper.openEndStatic(true);//open
  PrinterHelper.PrintData(data);//Printer is printing
  int endStatus = PrinterHelper.getEndStatus(16);//Get the print status
  PrinterHelper.openEndStatic(false);//close
  ```

  

------

#### 3.39 **Get the status when print completed**

- Description

  ```java
  int getEndStatus(int time)
  ```

- Parameter

  | Parameter | Description                                        |
  | :-------- | :------------------------------------------------- |
  | time      | timeout time of getting the status（unit：second） |

- Return

  | value | Description                                                  |
  | ----- | ------------------------------------------------------------ |
  | 0     | print success                                                |
  | 1     | print failure (out of paper)                                 |
  | 2     | print failure (cover opened)                                 |
  | -1    | getting status timeout(The printer did not respond within the set time) |

- Example

  ```java
  PrinterHelper.openEndStatic(true);//open
  PrinterHelper.PrintData(data);//Printer is printing
  int endStatus = PrinterHelper.getEndStatus(16);//Get the print status
  PrinterHelper.openEndStatic(false);//close
  ```

  

------

#### 3.40 **Printer go back**

- Description

  ```java
  int ReverseFeed(int feed)
  ```

- Parameter

  | Parameter | Description                                     |
  | :-------- | :---------------------------------------------- |
  | feed      | Fallback distance.（unit：row，range：1-255）。 |

- Return

  | value | Description  |
  | ----- | ------------ |
  | >0    | send success |
  | -1    | send failure |

- Example

  ```java
  PrinterHelper.ReverseFeed(50);
  ```

  

------

#### 3.41 **Print Background**

- Description

  ```java
  int PrintBackground(int x,int y,int size,int background,String data)
  ```

- Parameter

  | Parameter  | Description                                                  |
  | :--------- | :----------------------------------------------------------- |
  | feed       | X-axis（unit：dot）                                          |
  | y          | Y-axis（unit：dot）                                          |
  | size       | font size<br />55：16X16（dot）<br/>24：24X24（dot）<br/>56：32X32（dot）<br/>other：24X24（dot） |
  | background | Background blackness（0-255）                                |
  | data       | data                                                         |

- Return

  | value | Description  |
  | ----- | ------------ |
  | >0    | send success |
  | -1    | send failure |

- Example

  ```java
  PrinterHelper.printAreaSize("0","200","200","500","1");																 PrinterHelper.SetMag("8","8");//Font size up to 8 times
  PrinterHelper.PrintBackground(0,0,56,150,"A508");
  PrinterHelper.SetMag("1","1");//Restore font size
  PrinterHelper.Print();
  ```

  

------

#### 3.42 **Get SN**

- Description

  ```java
  String getPrintSN()
  ```

- Parameter
  Null

- Return

  | value | Description |
  | ----- | ----------- |
  | sn    | printer SN  |

- Example

  ```java
  PrinterHelper.getPrintSN();
  ```

  

------

#### 3.43 **Set Codepage**

- Description

  ```java
  int Country(String codepage)
  ```

- Parameter

  | Parameter | Description                                                  |
  | :-------- | :----------------------------------------------------------- |
  | codepage  | codepage<br />ISO8859-1 ：Western European<br />ISO8859-2 ：Latin（2）<br />ISO8859-3 ：Latin（3）<br />ISO8859-4 ：Baltic<br />ISO8859-5 ：Cyrillic<br />ISO8859-6 ：Arabic<br />ISO8859-8 ：Hebrew<br />ISO8859-9 ：Turkish<br />ISO8859-15 ：Latin（9）<br />WPC1253 ：Greek（windows）<br />KU42：Greek（ISO）<br />TIS18: Thai |

- Return

  | value | Description  |
  | ----- | ------------ |
  | >0    | send succeed |
  | -1    | send failure |

- Example

  ```java
  PrinterHelper.printAreaSize("0","200","200","500","1");
  PrinterHelper.Country("ISO8859-1");
  PrinterHelper.LanguageEncode="iso8859-1";
  PrinterHelper.PrintCodepageTextCPCL(PrinterHelper.TEXT,0,"10","10","TEXT",15)
  PrinterHelper.Print()
  ```
------

#### 3.44 **Set QRcode Version**

- Description
  

This interface is used to set the version number of the QR code. After setting, it will not change the size due to the content of the QR code.Only some models and versions are supported (you can ask customer service).

  ```java
  int setQRcodeVersion(int version)
  ```

- Parameter

  | Parameter | Description                                                  |
  | :-------- | :----------------------------------------------------------- |
  | version   | Version number (range 0-40)<br />The QR version defaults to 00. When the QR version is 00, the QR code has the same 	effect 	as the 	old version, and the width and height will vary with the amount of 	data.The version number is set to have a range requirement for the data amount of the 	two-dimensional code, and the out-of-range QR code is not printed.<br/>See the end of Table 1-1. |

- Return

  | value | Description     |
  | ----- | --------------- |
  | >0    | send success    |
  | -1    | send failure    |
  | -2    | Parameter error |

- Example

  ```java
  PrinterHelper.setQRcodeVersion(20);
  ```

  

------

#### 3.45 **Get QRcode Version**

- Description

  ```java
  String getQRcodeVersion()
  ```

- Parameter
  Null

- Return

  | value | Description                           |
  | ----- | ------------------------------------- |
  | >0    | QRcode Version（failure return null） |

- Example

  ```java
  String version = PrinterHelper.getQRcodeVersion();
  ```

  

------

#### 3.46 **Print text in line print mode**

- Description

  ```java
  int PrintData(String str)
  ```

- Parameter

  | Parameter | Description                      |
  | :-------- | :------------------------------- |
  | str       | content of text (ends with \r\n) |

- Return

  | value | Description   |
  | ----- | ------------- |
  | >0    | send successs |
  | -1    | send failure  |

- Example

  ```java
  PrinterHelper.PrintData("text to print\r\n")
  ```

  

------

#### 3.47 **Bold font in line print mode**

- Description

  ```java
  int RowSetBold(String bold)
  ```

- Parameter

  | Parameter | Description                                                  |
  | :-------- | :----------------------------------------------------------- |
  | bold      | multiple of bold<br />1：close bold<br />other：multiple of bold |

- Return

  | value | Description  |
  | ----- | ------------ |
  | >0    | send success |
  | -1    | send failure |

- Example

  ```java
  PrinterHelper.Setlp("5","2","46")
  PrinterHelper.RowSetBold("2")
  PrinterHelper.PrintData("text to print\r\n")
  //Note to turn off bold in case of affecting the following 	print text
  PrinterHelper.RowSetBold("1")
  ```
------

#### 3.48 **Set x-coordinate of line mode**

- Description
  It should be placed before Setlp function.

  ```java
  int RowSetX(String X)
  ```

- Parameter

  | Parameter | Description               |
  | :-------- | :------------------------ |
  | bold      | x-coordinate（unit：dot） |

- Return

  | value | Description  |
  | ----- | ------------ |
  | >0    | send success |
  | -1    | send failure |

- Example

  ```java
  PrinterHelper.RowSetX("200");
  PrinterHelper.Setlp("5","2","32");
  PrinterHelper.RowSetBold("2");
  PrinterHelper.PrintData("text to print\r\n");
  PrinterHelper.RowSetBold("1");
  ```

  

------



#### 3.49 **Write RFID**

- Description

  ```java
  printRFIDCPCL(List<RFIDBeen> rfidBeens,Bitmap bitmap,int x,int y,int type,
                int compressType,int density)
  ```

- Parameter

  | Parameter    | Description                                                  |
  | :----------- | :----------------------------------------------------------- |
  | rfidBeens    | RFID object set                                              |
  | memory       | Storage area, 0: Retention area 1:EPC area, 3: User area     |
  | address      | memory =0-->address(0-3), <br/>memory =1--> address (2-7), <br/>memory=3-->address(0-255) |
  | data         | write data，memory=0-->（address+data length<=4）、<br/>	                  memory=1-->（address+data length<=26）、<br/>	                  memory=3-->(address+data length<=256) |
  | bitmap       | Image objects                                                |
  | x            | abscissa                                                     |
  | y            | ordinate                                                     |
  | type         | image algorithm, 0: binary algorithm, 1: halftone algorithm  |
  | compressType | Type of compression, 0: non-compression, 1: overall compression, 2: subcontract compression |
  | density      | Concentration (-1, no concentration set)                     |

- Return

  | value | Description                                             |
  | ----- | ------------------------------------------------------- |
  | 0     | send success                                            |
  | -1    | send failure                                            |
  | -2    | Parameter error                                         |
  | -3    | The image data is too large for the printer to compress |
  | -4    | RFID write failed (printer return)                      |

- Example

  ```java
  List<RFIDBeen> rfidBeenList = new ArrayList<>();
          RFIDBeen rfidBeen = new RFIDBeen();
          rfidBeen.setMemory(1);
          rfidBeen.setAddress(2);
          rfidBeen.setData("123456");
          rfidBeenList.add(rfidBeen);
          Log.d("Print", "testRFID: "+PrinterHelper.printRFIDCPCL(rfidBeenList,null,0,0,0,0,0));
  ```

  

------

​		

#### 3.50 **Read RFID**

- Description

  ```java
  readRFIDCPCL(List<RFIDBeen> rfidBeens)
  ```

- Parameter

  | Parameter | Description                                                  |
  | :-------- | :----------------------------------------------------------- |
  | rfidBeens | RFID object set                                              |
  | memory    | Storage area, 0: Retention area 1:EPC area, 3: User area     |
  | address   | memory =0-->address(0-3), <br/>memory =1--> address (2-7), <br/>memory =2--> address (2-7), <br/>memory=3-->address(0-255) |
  | length    | memory=0-->length(1-8)、<br />memory=1-->length(1-24)、<br />memory=2-->length(1-24)、<br />memory=3-->length(1-256) |

- Return

  | value          | Description                                     |
  | -------------- | ----------------------------------------------- |
  | List<RFIDBeen> | Collection of RFID objects (empty means failed) |
  | data           | Read data                                       |

- Example

  ```java
   List<RFIDBeen> rfidBeenList = new ArrayList<>();
          RFIDBeen rfidBeen = new RFIDBeen();
          rfidBeen.setMemory(1);
          rfidBeen.setAddress(2);
          rfidBeen.setLength(24);
          rfidBeenList.add(rfidBeen);
          List<RFIDBeen> rfidBeenListResult = PrinterHelper.readRFIDCPCL(rfidBeenList);
          if (rfidBeenListResult==null){
              Log.d("Print", "testRFIDRead: null");
              return;
          }
          for (int i = 0; i < rfidBeenListResult.size(); i++) {
              Log.d("Print", "testRFIDRead: "+rfidBeenListResult.get(i).toString());
              ToastUtility.show(mContext,rfidBeenListResult.get(i).toString());
          }
  ```

  

------



#### 3.51 **Set Bluetooth Name**

- Description

  Modify the name of Bluetooth, mainly to use the saving interface together

  ```java
  int setBluetoothName(String name)
  ```

- Parameter

  | Parameter | Description                                                  |
  | :-------- | :----------------------------------------------------------- |
  | name      | The Bluetooth name that needs to be modified (cannot be Chinese, and the length cannot exceed 32) |

- Return

  | value | Description     |
  | ----- | --------------- |
  | > 0   | send success    |
  | -1    | send failure    |
  | -2    | Parameter error |

- Example

  ```java
  PrinterHelper.setBluetoothName(data);
  PrinterHelper.saveParameter();
  ```

  

------



#### 3.52 **Set  Save**

- Description

  ```java
  int saveParameter()
  ```

- Parameter

  null

- Return

  | value | Description  |
  | ----- | ------------ |
  | > 0   | send success |
  | -1    | send failure |

- Example

  ```java
  PrinterHelper.setBluetoothName(data);
  PrinterHelper.saveParameter();
  ```

  

------



#### 3.53 **Set Double Color Print**

- Description

  ```java
  int setLayer(int layer)
  ```

- Parameter

  | Parameter | Description                                           |
  | :-------- | :---------------------------------------------------- |
  | layer     | The colors that need to be printed, 0: red, 1: black. |

- Return

  | value | Description  |
  | ----- | ------------ |
  | 0     | send success |
  | -1    | send failure |

- Example

  ```java
  PrinterHelper.printAreaSize("0","200","200","100","1");
  PrinterHelper.setLayer(0);  
  PrinterHelper.Text(PrinterHelper.TEXT,"4","0","0","0","TEXT");
  PrinterHelper.setLayer(1);  
  PrinterHelper.Text(PrinterHelper.TEXT,"4","0","0","50","TEXT");
  PrinterHelper.Form();
  PrinterHelper.PoPrint();
  ```

  

------



#### 3.54 **Get  Charge**

- Description

  ```java
  int getElectricity()
  ```

- Parameter

  null

- Return

  | value | Description          |
  | ----- | -------------------- |
  | > 0   | percentage of charge |
  | -1    | send failure         |

- Example

  ```java
  PrinterHelper.getElectricity();
  ```

  

------



#### 3.55 **Get  Voltage**

- Description

  ```java
  String getVoltage()
  ```

- Parameter

  null

- Return

  | value   | Description             |
  | ------- | ----------------------- |
  | no null | Voltage (format: x.xxV) |
  | null    | send failure            |

- Example

  ```java
  PrinterHelper.getVoltage();
  ```

  

------



#### 3.56 **Listener  Printer Bluetooth To Disconnect**

- Description

	```java
	void setDisConnectBTListener(DisConnectBTListener disConnectBTListener)
	```

- Parameter

	| value                | Description                                  |
	| :------------------- | :------------------------------------------- |
	| disConnectBTListener | Bluetooth disconnects the callback interface |

- Return

	null

- Example

	```java
	PrinterHelper.setDisConnectBTListener(disConnectBTListener);
	private DisConnectBTListener disConnectBTListener = () -> runOnUiThread(() -> {
			txtTips.setText("BT Disconnect");
			Toast.makeText(thisCon, "BT Disconnect", Toast.LENGTH_SHORT).show();
	});		
	```

	

------



#### 3.57 **Get Printer Version**

- Description

	```java
	String getPrinterVersion()
	```

- Parameter

	null

- Return

	| value   | Description     |
	| ------- | --------------- |
	| version | printer version |

- Example

	```java
	String version = PrinterHelper.getPrinterVersion()
	```

	

------





#### Tab 1-1

| Version | Error Level | Number of data      |
| ------- | ----------- | ------------------- |
| 1       | L M Q H     | 19 16 13 9          |
| 2       | L M Q H     | 34 28 22 16         |
| 3       | L M Q H     | 55 44 34 26         |
| 4       | L M Q H     | 80 64 48 36         |
| 5       | L M Q H     | 108 86 62 46        |
| 6       | L M Q H     | 136 108 76 60       |
| 7       | L M Q H     | 156 124 88 66       |
| 8       | L M Q H     | 194 154 110 86      |
| 9       | L M Q H     | 232 182 132 100     |
| 10      | L M Q H     | 274 216 154 122     |
| 11      | L M Q H     | 324 254 180 140     |
| 12      | L M Q H     | 370 290 206 158     |
| 13      | L M Q H     | 428 334 244 180     |
| 14      | L M Q H     | 461 365 261 197     |
| 15      | L M Q H     | 523 415 295 223     |
| 16      | L M Q H     | 589 453 325 253     |
| 17      | L M Q H     | 647 507 367 283     |
| 18      | L M Q H     | 721 563 397 313     |
| 19      | L M Q H     | 795 627 445 341     |
| 20      | L M Q H     | 861 669 485 385     |
| 21      | L M Q H     | 932 714 512 406     |
| 22      | L M Q H     | 1006 782 568 442    |
| 23      | L M Q H     | 1094 860 614 464    |
| 24      | L M Q H     | 1174 914 664 514    |
| 25      | L M Q H     | 1276 1000 718 538   |
| 26      | L M Q H     | 1370 1062 754 596   |
| 27      | L M Q H     | 1468 1128 808 628   |
| 28      | L M Q H     | 1531 1193 871 661   |
| 29      | L M Q H     | 1631 1267 911 701   |
| 30      | L M Q H     | 1735 1373 985 745   |
| 31      | L M Q H     | 1843 1455 1033 793  |
| 32      | L M Q H     | 1955 1541 1115 845  |
| 33      | L M Q H     | 2071 1631 1171 901  |
| 34      | L M Q H     | 2191 1725 1231 961  |
| 35      | L M Q H     | 2306 1812 1286 986  |
| 36      | L M Q H     | 2434 1914 1354 1054 |
| 37      | L M Q H     | 2566 1992 1426 1096 |
| 38      | L M Q H     | 2702 2102 1502 1142 |
| 39      | L M Q H     | 2812 2216 1582 1222 |
| 40      | L M Q H     | 2956 2334 1666 1276 |