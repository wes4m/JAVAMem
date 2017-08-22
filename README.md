# JAVAMEM #

Demonstration of using java sockets. to do remote code execution on the fly from memory using plugins concept.

---

### Server programing ###

* Connect normally to given port.
* When sending plugins/large data over specified(**max_buffer_size**) :
    - Send ``BSP`` to begin splitting process.
    - Send plugin source bytes.
    - Send ``ESP`` to end splitting process.
    - Send ``LoadPLG^PluginClassName``.
    - When server receive ``BSP`` prepare to receive large data chunks.
    - When server receive ``ESP`` end receiving large data chunks.

---

* **Plugin commands**:
    - ``ListPLG`` : Returns list of loaded plugins.
    - ``ListPLGF^PluginClassName`` : Returns list of specific plugin functions.
    - ``CallPLGF^PluginClassName^FuncName^JsonStr`` : Executes function.
    - ``CallPLGD^PluginClassName^FuncName^X`` : Executes setData function.

### Plugin programing ###
* Plugin must be in java. 
* Plugin must follow this function declaration template : 

```
#!java

public String functionName(String JsonStr)
{
   // do work
   return ...;
}

```

* Plugin must include this function :

```
#!java

public void setData(char[] data)
{

}

```
**This function is called when ``CallPLGD`` is called. To pass large data to plugin.**


#### Example plugin ####
```
#!java

public class TestPlugin
{
    public String getOS(String non)
    {
        return System.getProperty("os.name");
    }

    char[] lastData;
    public void setData(char[] data)
    {
       lastData = new char[data.length];
       lastData = data;
    }
}

```

### Credits ###
Ally Please insert doge e-god here :camel:
