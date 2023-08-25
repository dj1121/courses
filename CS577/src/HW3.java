/* Algorithm:
Input: An array of integers representing the leaf nodes of complete binary tree.
Output: The arrangement of integers which minimizes inversions while adhering
to the swap operation in the problem outline. */

import java.util.Arrays;

public class HW3{

    static int inversionsLR = 0;
    static int inversionsRL = 0;
    static int inversions = 0;

    public static int[][] minimizeInversions(int[] a, int[] sorted){
        if(a.length == 1){
            int[][] combined ={a,sorted};
            return combined;
        }
        else{
            int[][] left = minimizeInversions(Arrays.copyOfRange(a, 0, a.length / 2),sorted);
            int[][] right = minimizeInversions(Arrays.copyOfRange(a, a.length / 2, a.length), sorted);

            if(left[0].length == 1){
                sorted = merge(left[0],right[0]);
            }
            else{
                sorted = merge(left[1],right[1]);
            }

            // Return whatever configuration yielded better num. of inversions (LR or RL)
            int[] c = new int[left[0].length + right[0].length];
            if(inversionsLR < inversionsRL){
                inversions += inversionsLR;
                for(int i = 0; i  < left[0].length; i++){
                    c[i] = left[0][i];
                }
                for(int i = c.length - (c.length/2); i < c.length; i++){
                    c[i] = right[0][i-(c.length/2)];
                }
            }
            else{
                inversions += inversionsRL;
                for(int i = 0; i  < right[0].length; i++){
                    c[i] = right[0][i];
                }
                for(int i = c.length - (c.length/2); i < c.length; i++){
                    c[i] = left[0][i-(c.length/2)];
                }
            }

            int[][] combined ={c,sorted};
            return combined;
        }
    }

    public static int[] merge(int[] l, int[] r){

        int[] c = new int[l.length + r.length];
        int lIndex = 0;
        int rIndex = 0;
        int cIndex = 0;
        int inversionCount = 0;

        // Copy in order and count inversions
        while (lIndex < l.length && rIndex < r.length) {
            if (l[lIndex] <= r[rIndex]) {
                c[cIndex] = l[lIndex];
                lIndex++;
            }
            else {
                c[cIndex] = r[rIndex];
                rIndex++;
                inversionCount = inversionCount + (l.length - lIndex);
            }
            cIndex++;
        }

        // Copy remaining
        while(lIndex < l.length){
            c[cIndex] = l[lIndex];
            lIndex++;
            cIndex++;
        }
        while(rIndex< r.length) {
            c[cIndex] = r[rIndex];
            rIndex++;
            cIndex++;
        }
        inversionsLR = inversionCount;
        inversionsRL = (int)Math.pow(l.length, 2) - inversionsLR;
        return c;
    }

    public static void main(String[] args){
        int[] test = new int[]{40, 23, 3, 36, 43, 2, 70, 19, 82, 96, 75, 91, 73, 56, 59, 9, 90, 31, 81, 32, 35, 28, 14, 4, 97, 55, 7, 49, 6, 94, 67, 22};
        int[] sorted = null;
        int[][]combined = minimizeInversions(test,sorted);
        System.out.println(Arrays.toString(combined[0]));
        System.out.println(Arrays.toString(combined[1]));
        System.out.println(inversions);
    }

}

