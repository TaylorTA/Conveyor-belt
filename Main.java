import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args){
        Scanner keyboard;
        String fileName;
        // Allow user to choose file with keyboard input.
        keyboard = new Scanner( System.in );
        System.out.println( "\nEnter the input file name (.txt files only): " );
        fileName = keyboard.nextLine();
        System.out.println("Processing file....");

        // read the file
        try{
            BufferedReader br = new BufferedReader(new FileReader(fileName));

            // create the conveyor belt for baggage
            ConveyorBelt cb = new ConveyorBelt();

            String line = br.readLine();
            while(line!= null){
                System.out.println("=====Processing command: "+line+ " =====");

                // separate the command
                String[] tokens = line.split(" ");

                // process command "PRINTSUMMARY"
                if(tokens[0].equals("PRINTSUMMARY")){
                    System.out.println("Total number of bags: " + cb.getBagN() + ", Number of VIP bags: " + cb.getVIPBagN() + ", Total weight of bags: " + cb.getTotalWeight());
                }

                // process command "check bags"
                else if(tokens[0].equals("CHECKBAGS")){
                    int bagN = Integer.parseInt(tokens[1]);
                    int vipN = 0;
                    int notVip = 0;

                    // add bags
                    for(int i=0; i<bagN; i++){
                        line = br.readLine();
                        // create new Baggage to be process
                        Baggage newBaggage = new Baggage(line);
                        // add new baggage to conveyor belt
                        cb.add(newBaggage);
                        tokens = line.split(" ");
                        if(tokens[tokens.length-1].equals("true"))
                            vipN++;
                        else
                            notVip++;
                    }
                    System.out.println(vipN + " VIP and " + notVip + " regular bags checked in.");
                }

                // process command "PRINTDETAIL"
                else if(tokens[0].equals("PRINTDETAIL")){
                    System.out.println("Total number of bags: " + cb.getBagN() + ", Number of VIP bags: " + cb.getVIPBagN() + ", Total weight of bags: " + cb.getTotalWeight());
                    System.out.println("The bags on the conveyor belt are:");
                    System.out.print(cb);
                }

                // process command "LOADFLIGHT"
                else if(tokens[0].equals("LOADFLIGHT")){
                    System.out.println(cb.loadFlight(Integer.parseInt(tokens[1])) + " bags loaded onto flight " + tokens[1] + ".");
                }

                // process command "REMOVEOVERSIZE"
                else if(tokens[0].equals("REMOVEOVERSIZE")){
                    System.out.println(cb.removeSize(Double.parseDouble(tokens[1]))+ " bags removed as oversized.");
                }

                line = br.readLine();
            }
        }catch (IOException ioe){
            System.out.println(ioe.getMessage());
        }

        System.out.println("\nEnd of Processing.");
    }
}

class ConveyorBelt {
    private Node start;
    private Node end;

    public ConveyorBelt(){
        start = null;
        end = null;
    }

    public void add(Baggage newBaggage) {
        if(!newBaggage.isVIP()){
            Node newEnd = new Node(newBaggage,end,null);
            // if there is one or more baggage in the belt
            if(end!=null)
                end.setNext(newEnd);
                // if there is no baggage in the belt
            else
                start = newEnd;
            end = newEnd;
        }else{
            // if no baggage in the belt
            if(start == null){
                Node newStart = new Node(newBaggage,null,null);
                start = newStart;
                end = newStart;
            }else{
                Node curr = start;
                while(curr!=null&&curr.isVIP()){
                    curr = curr.getNext();
                }
                // if the start is not VIP
                if(curr == start){
                    Node newStart = new Node(newBaggage,null,start);
                    start.setPrev(newStart);
                    start = newStart;
                }
                // if the curr pos is at the end of belt
                else if(curr == null){
                    Node newEnd = new Node(newBaggage,end,null);
                    end.setNext(newEnd);
                    end = newEnd;
                }
                // if the curr pos is in the middle
                else{
                    Node newMiddle = new Node(newBaggage,curr.getPrev(),curr);
                    curr.getPrev().setNext(newMiddle);
                    curr.setPrev(newMiddle);
                }
            }
        }
    }

    @Override
    public String toString() {
        String result = "";
        Node curr = start;
        int i = 0;
        while(curr!=null) {
            result += i+1 + ") " + curr + "\n";
            curr = curr.getNext();
            i++;
        }
        return result;
    }

    public int getBagN() {
        int bagN = 0;
        Node curr = start;
        while (curr!=null){
            bagN++;
            curr = curr.getNext();
        }
        return bagN;
    }

    public int getVIPBagN(){
        int bagN = 0;
        Node curr = start;
        while (curr!=null){
            if(curr.isVIP())
                bagN++;
            curr = curr.getNext();
        }
        return bagN;
    }

    public float getTotalWeight(){
        float weight = 0;
        Node curr = start;
        while (curr!=null){
            weight += curr.getItem().getWeight();
            curr = curr.getNext();
        }
        return weight;
    }

    public int loadFlight(int flightN) {
        Node curr = start;
        int count = 0;
        while(curr!=null){
            // if the curr node is the node to be removed
            if(curr.getItem().getFlightN() == flightN){
                // if curr is start
                if(curr == start){
                    start = curr.getNext();
                    if(start == null)
                        end = null;
                    else
                        start.setPrev(null);
                }
                // if curr is end
                else if(curr == end){
                    end = curr.getPrev();
                }
                // if curr is in the middle
                else{
                    curr.getPrev().setNext(curr.getNext());
                    curr.getNext().setPrev(curr.getPrev());
                }
                count ++;
            }
            curr = curr.getNext();
        }
        return count;
    }

    public int removeSize(double size) {
        Node curr = start;
        int count = 0;
        while(curr!=null){
            // if the curr node is the node to be removed
            if(curr.getItem().getSize() > size){
                // if curr is start
                if(curr == start){
                    start = curr.getNext();
                    if(start == null)
                        end = null;
                    else
                        start.setPrev(null);
                }
                // if curr is end
                else if(curr == end){
                    end = curr.getPrev();
                }
                // if curr is in the middle
                else{
                    curr.getPrev().setNext(curr.getNext());
                    curr.getNext().setPrev(curr.getPrev());
                }
                count ++;
            }
            curr = curr.getNext();
        }
        return count;
    }
}

class Node{
    private Baggage item;
    private Node prev;
    private Node next;


    public Node(Baggage item, Node prev, Node next){
        this.item = item;
        this.prev = prev;
        this.next = next;
    }

    public Node(Baggage item){
        this.item = item;
        prev = null;
        next = null;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }

    public boolean isVIP() {
        return item.isVIP();
    }

    public Node getNext() {
        return next;
    }

    public Node getPrev() {
        return prev;
    }

    public Baggage getItem() {
        return item;
    }

    @Override
    public String toString() {
        return item.toString();
    }
}

class Baggage {
    private int flightN;
    private float weight;
    private float size;
    private boolean isVIP;

    public Baggage(int flightN, float weight, float size, boolean isVIP){
        this.flightN = flightN;
        this.weight = weight;
        this.size = size;
        this.isVIP = isVIP;
    }

    public Baggage(String line){
        String[] tokens = line.split(" ");
        flightN = Integer.parseInt(tokens[0]);
        weight = Float.parseFloat(tokens[1]);
        size = Float.parseFloat(tokens[2]);
        isVIP = Boolean.parseBoolean(tokens[3]);
    }

    public boolean isVIP() {
        return isVIP;
    }

    public float getWeight() {
        return weight;
    }

    public int getFlightN() {
        return flightN;
    }

    public float getSize() {
        return size;
    }

    @Override
    public String toString() {
        return "Flight number: " + flightN + ", Weight: " + weight + " kg, Size: " + size + " cm, VIP: " + isVIP;
    }
}



