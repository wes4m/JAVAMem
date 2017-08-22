package dotnetClient.Main;

// Imports
import java.lang.reflect.Method;
import java.util.List;
import java.util.ArrayList;
import dotnetClient.Connection.Client;
import dotnetClient.Compiler.InMemoryJavaCompiler;

public class CommandManager {

    // Plugin Manager vars
    private List<String> plugins = new ArrayList<String>();
    private List<Class<?>> compiledPlugins = new ArrayList<Class<?>>();
    private List<Object> pluginsInstances = new ArrayList<Object>();

    private char[] lastBuff;
    private Client client;

    public CommandManager(Client client) {
        this.client = client;
    }

    public void command(String command) {
        // var to be used to return data
        String ret = "";

        try {
            // index[0] = command
            String[] data = command.split("\\^"); // using "^" to split

            switch (data[0]) {
                case "Hi":
                    /*  Func : Welcoming the mother fucker
                        Params : None
                    */
                    client..getSocket().send("Helllllo .. !!");
                    break;
                case "ListPLG":
                    /*  Func : Returns list of loaded plugins
                        Params : None
                    */
                    ret = "ListPLG^";
                    for(String plugin : plugins) {
                        ret += plugin + "^";
                    }
                    client..getSocket().send(ret);
                    break;
                case "ListPLGF":
                    /*  Func : Returns list of specfic plugin funcs
                        Params :
                         [1] Plugin class name
                    */
                    ret = "ListPLGF^";
                    Method[] allMethods = PluginClass(data[1]).getDeclaredMethods();
                    for (Method method : allMethods) {
                        if (!method.getName().equalsIgnoreCase("setData")) {
                            ret += method.getName() + "^";
                        }
                    }
                    client..getSocket().send(ret);
                    break;
                case "CallPLGD":
                    /*  Func : Calls a plugin big data function (setData)
                        Params :
                         [1] Plugin class name
                    */
                    Method[] declaredMethods = PluginClass(data[1]).getDeclaredMethods();
                    for (Method method : declaredMethods) {
                        if (method.getName().equalsIgnoreCase("setData")) {
                            // found method
                            // Create params object
                            Object[] params = new Object[]{lastBuff};
                            ret = "RetPLG^" + data[1].trim() + "^" + method.getName() + "^";
                            ret += (String)method.invoke(PluginInstance(data[1]), params);
                            client..getSocket().send(ret);
                        }
                    }
                    break;
                case "CallPLGF":
                    /*  Func : Calls a plugin fucntion
                        Params :
                         [1] Plugin class name
                         [2] Function name
                         [3] Json Params string
                    */
                    Method[] declaredMethods = PluginClass(data[1]).getDeclaredMethods();
                    for(Method method : declaredMethods) {
                        if (method.getName().equalsIgnoreCase(data[2].trim())) {
                            // found method
                            // Create params object
                            Object[] params = new Object[]{data[3].trim()};
                            ret = "RetPLG^" + data[1].trim() + "^" + meth.getName() + "^";
                            ret += (String)meth.invoke(PluginInstance(data[1]), params);
                            client..getSocket().send(ret);
                        }
                    }
                    break;
                case "LoadPLG":
                    /*  Func : Compiling and loading plugin in memory
                        Params :
                         [1] Plugin class name
                    */
                    String src = new String(lastBuff).trim();
                    Class<?> compiled = InMemoryJavaCompiler.compile(data[1].trim(), src);
                    plugins.add(data[1].trim());
                    compiledPlugins.add(compiled);
                    pluginsInstances.add(compiled.newInstance());
                    break;
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
          // Error
        }
    }

    public void setLastBuff(char[] data) {
        lastBuff = new char[data.length];
        lastBuff = data;
    }

    // change to getPluginIndex
    private int PluginIndex(String name) {
        return strPlugins.indexOf(name.trim());
    }

    private Class<?> PluginClass(String name) {
        return compiledPlugins.get(PluginIndex(name));
    }

    private Object PluginInstance(String name) {
        return pluginsInstances.get(PluginIndex(name));
    }
}
