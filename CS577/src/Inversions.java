import java.io.File;
import java.util.*;
import java.lang.*;

class Inversions {
    public static void main (String[] args) throws java.lang.Exception{
        try{
            Scanner input = new Scanner(new File("input.txt"));
            int numLines = 0;
            int[] myList = new int[1];

            // Get number of lines
            if(input.hasNextInt()){
                numLines = input.nextInt() * 2;
                input.nextLine();
            }
            else{
                System.out.println("Error: wrong format");
                return;
            }

            // For each line
            int lineLength = 0;
            for(int i = 1; i <= numLines; i++){
                if(i % 2 == 0){
                    for(int j = 0; j < lineLength && input.hasNextInt(); j++){
                        myList[j] = input.nextInt();
                    }
                    System.out.println(sortCount(myList, new int[myList.length],0, myList.length -1));
                }
                else{
                    lineLength = input.nextInt();
                    myList = new int[lineLength];

                }
                if(input.hasNextLine()){
                    input.nextLine();
                }
            }
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
        }
    }

    public static int sortCount(int a[], int b[], int l, int r){
        int m = 0;
        int inversions = 0;
        if (r > l){
            m = (r + l) / 2;
            // Count inversions in left, right, and then during merge (divide and conquer)
            inversions = sortCount(a, b, l, m);
            inversions += sortCount(a, b, m + 1, r);
            inversions += merge(a, b, l, m + 1, r);
        }
        return inversions;
    }

    public static int merge(int a[], int b[], int l, int m, int r){
        int i = l;
        int j = m;
        int k = l;
        int inversions = 0;
        // Traverse through left and right halves
        while(i <= m - 1 && j <= r){
            // Copy end output if smaller on left half
            if (a[i] <= a[j]) {
                b[k] = a[i];
                k++; i++;
            }
            // Copy end output if smaller on right half
            else{
                b[k] = a[j];
                k++; j++;
                inversions = inversions + (m - i);
            }
        }
        while (i <= m - 1) {
            b[k] = a[i];
            k++; i++;
        }

        while (j <= r) {
            b[k] = a[j];
            k++; j++;
        }

        for (i = l; i <= r; i++) {
            a[i] = b[i];
        }
        return inversions;
    }
}