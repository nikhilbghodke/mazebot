import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.management.OperatingSystemMXBean;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Main {

    static char [][] maze;
    static String name;
    static int [] start= new int[2];
    static int [] end= new int[2];
    static  int size;
    static String mazePath;
    public static void main(String[] args) throws IOException {

        //Making Post Request to start the race.......
        String baseUrl = "https://api.noopschallenge.com";
        URL url1 = new URL("https://api.noopschallenge.com/mazebot/race/start");
        URLConnection con1 = url1.openConnection();
        HttpURLConnection http1 = (HttpURLConnection)con1;
        http1.setRequestMethod("POST"); // PUT is another valid option
        http1.setDoOutput(true);

        //Writing Json String....
        byte[] out1 = "{\"login\":\"nikhilbghodke\"}" .getBytes(StandardCharsets.UTF_8);
        int length1 = out1.length;
        http1.setFixedLengthStreamingMode(length1);
        http1.setRequestProperty("Content-Type", "application/json");
        http1.connect();
        try(OutputStream os = http1.getOutputStream()) {
            os.write(out1);
        }
        BufferedReader br1 = new BufferedReader(new InputStreamReader(http1.getInputStream()));
        StringBuilder sb1 = new StringBuilder();
        String line1="";
        while ((line1 = br1.readLine()) != null) {
            sb1.append(line1 + "\n");
        }
        br1.close();
        System.out.println(sb1.toString());
        JsonObject jsonObject1 = new Gson().fromJson(sb1.toString(), JsonObject.class);
        String url=baseUrl+jsonObject1.get("nextMaze").getAsString();

       // String url=baseUrl+"/mazebot/race/9vHkE1PU1MGlGGa3Ph1hkx4WBfpqIOvmjFXtNflQdfxWjw7I5ZwyUp2s52PpVHIN";
        while(true){
        HttpURLConnection httpClient =
                (HttpURLConnection) new URL(url).openConnection();
        httpClient.setRequestMethod("GET");
        int responseCode = httpClient.getResponseCode();
        System.out.println(responseCode);
        BufferedReader br = new BufferedReader(new InputStreamReader(httpClient.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }
        br.close();
        System.out.println(sb.toString());

        //Parsing and storing Json Elements...
        JsonObject jsonObject = new Gson().fromJson(sb.toString(), JsonObject.class);
        System.out.println(jsonObject.get("name"));
        name=jsonObject.get("name").toString();
        start[0]=jsonObject.get("startingPosition").getAsJsonArray().get(0).getAsInt();
        start[1]=jsonObject.get("startingPosition").getAsJsonArray().get(1).getAsInt();
        end[0]=jsonObject.get("endingPosition").getAsJsonArray().get(0).getAsInt();
        end[1]=jsonObject.get("endingPosition").getAsJsonArray().get(1).getAsInt();
        mazePath=jsonObject.get("mazePath").getAsString();
        //System.out.println(jsonObject.get("map").getAsJsonArray());
        JsonArray jsonArray= jsonObject.get("map").getAsJsonArray();
         size=jsonArray.size();
         maze=new char[size][size];
        for(int i=0;i<size;i++)
        {
            JsonArray a=jsonArray.get(i).getAsJsonArray();
            for(int j=0;j<size;j++)
                maze[i][j]=a.get(j).getAsCharacter();
        }

       // System.out.println(maze[end[1]][end[0]]);
        //System.out.println(maze[start[1]][start[0]]);
        for(int i=0;i<size;i++) {
//            for (int j = 0; j < size; j++)
//                System.out.print(maze[i][j]);
//            System.out.print("\n");
        }

        //algo starts here
        Node[][] dij= new Node[size][size];
        for(int i=0;i<size;i++)
            for(int j=0;j<size;j++)
                dij[i][j]= new Node();
        //dij[start[1]][start[0]].visited=true;
        dij[start[1]][start[0]].v=0;
        dij[start[1]][start[0]].path='q';

        while(true)
        {
            int lx=0,ly=0;
            int lv=Integer.MAX_VALUE;

            for(int i=0;i<size;i++) {
                for (int j = 0; j < size; j++) {
                    if (!dij[i][j].visited&&dij[i][j].v < lv) {
                        lx = i;
                        ly = j;
                        lv = dij[i][j].v;
                    }
                }
            }

           // System.out.println(maze[lx][ly]);
            dij[lx][ly].visited=true;

            //North
            if(lx-1>=0&&maze[lx-1][ly]!='X'&& !dij[lx-1][ly].visited) {
                if(dij[lx-1][ly].v>dij[lx][ly].v+1){
                    dij[lx-1][ly].v=dij[lx][ly].v+1;
                    dij[lx-1][ly].path='N';
                }
                if(maze[lx-1][ly]=='B')
                    break;
            }

            //South
            if(lx+1<size&&maze[lx+1][ly]!='X'&& !dij[lx+1][ly].visited) {
                if(dij[lx+1][ly].v>dij[lx][ly].v+1){
                    dij[lx+1][ly].v=dij[lx][ly].v+1;
                    dij[lx+1][ly].path='S';
                }
                if(maze[lx+1][ly]=='B')
                    break;
            }

            //  West
            if(ly-1>=0&&maze[lx][ly-1]!='X'&& !dij[lx][ly-1].visited) {
                if(dij[lx][ly-1].v>dij[lx][ly].v+1){
                    dij[lx][ly-1].v=dij[lx][ly].v+1;
                    dij[lx][ly-1].path='W';
                }
                if(maze[lx][ly-1]=='B')
                    break;
            }
            //  East
            if(ly+1<size&&maze[lx][ly+1]!='X'&& !dij[lx][ly+1].visited) {
                if(dij[lx][ly+1].v>dij[lx][ly].v+1){
                    dij[lx][ly+1].v=dij[lx][ly].v+1;
                    dij[lx][ly+1].path='E';
                }
                if(maze[lx][ly+1]=='B')
                    break;
            }
        }

      //  System.out.println(dij[end[1]][end[0]].path);
        //path tracing
        char last='a';
        String ans="";
        int lx=end[1];
        int ly=end[0];
        int i=0;
        while(true){

            last=dij[lx][ly].path;
            if(last=='q')
                break;
            ans+=last;

            if(last=='W')
                ly++;
            else if(last=='E')
                ly--;
            else if(last=='N')
                lx++;
            else
                lx--;
            //System.out.println(last+":-   "+lx+","+ly);
            i++;
        }

        StringBuilder input1 = new StringBuilder();

        // append a string into StringBuilder input1
        input1.append(ans);

        // reverse StringBuilder input1
        input1 = input1.reverse();
        System.out.println(input1.toString());
        URL postUrl = new URL ("https://api.noopschallenge.com"+mazePath);
        URLConnection con = postUrl.openConnection();
        HttpURLConnection http = (HttpURLConnection)con;
        http.setRequestMethod("POST"); // PUT is another valid option
        http.setDoOutput(true);
        String jsonInputString = "{\n\"directions\":\""+input1.toString()+"\"\n}";
        byte[] out = jsonInputString.getBytes(StandardCharsets.UTF_8);
        int length = out.length;

        http.setFixedLengthStreamingMode(length);
        http.setRequestProperty("content-type", "application/json");
        http.connect();
        try(OutputStream os = http.getOutputStream()) {
            os.write(out);
        }
         responseCode = http.getResponseCode();
         br = new BufferedReader(new InputStreamReader(http.getInputStream()));
         sb = new StringBuilder();
         line="";
        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }
        br.close();
        System.out.println(sb.toString());
        jsonObject = new Gson().fromJson(sb.toString(), JsonObject.class);
        url=baseUrl+jsonObject.get("nextMaze").getAsString();
        System.out.println(responseCode);
        }
    }
}
abfghijkmnoll