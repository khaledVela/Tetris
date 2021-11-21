package List.diccionario;

import java.io.*;
import java.util.*;

public class Escribe_Lee {

    public void writeFile(List<String> lines) throws IOException {
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("src/main/java/Imagen/ListaJugadores")));
        if(lines.size()>10){
            for (int i=0;i<10;i++){
                out.println(lines.get(i));
            }
        }else{
            for (String line : lines) {
                out.println(line);
            }
        }
        out.close();
    }

    public List readFile() throws IOException {
        List list = new ArrayList();
        FileReader in = new FileReader("src/main/java/Imagen/ListaJugadores");
        BufferedReader br = new BufferedReader(in);
        String linea;
        while ((linea = br.readLine()) != null)
            list.add(linea);
        return list;
    }

    public void sob(String id,int x){
        id=id.toUpperCase(Locale.ROOT);
        Diccionario<String, Integer> dic1 = new DiccionarioSecuencia<>();
        List list = null;
        try {
            list = readFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < list.size(); i++) {
            String a = String.valueOf(list.get(i));
            String[] w = a.split(" ");
            dic1.insertar(w[0], Integer.valueOf(w[1]));
        }

        if(dic1.contineLlave(id)){
            if(x>dic1.obtener(id)){
                dic1.insertar(id, x);
            }
        }else{
            dic1.insertar(id, x);
        }

        list=new ArrayList();
        List orden=dic1.getValores();
        List key=dic1.getLlaves();
        Collections.sort(orden);
        Collections.reverse(orden);
        int w=0,y=0;
        while (list.size()!=orden.size()){
            if(orden.get(y)==dic1.obtener(String.valueOf(key.get(w)))){
                list.add(key.get(w)+" "+orden.get(y));
                y++;
            }
            w++;
            if(w==orden.size()){
                w=0;
            }
            //list.add(dic1.getLlaves().get(i)+" "+dic1.getValores().get(i));
        }
        try {
            writeFile(list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        List li = new ArrayList();
        Escribe_Lee tt = new Escribe_Lee();
        tt.sob("tT10", 10);
    }
}
