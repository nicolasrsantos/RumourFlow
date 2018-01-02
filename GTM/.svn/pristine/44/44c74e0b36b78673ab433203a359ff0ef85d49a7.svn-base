package org.textsim.textrt.proc.SMP;

/**
 * This class creates an UptrianularMatrix and provides an method return the corresponding cell of a given index
 *
 */
public class UptriangularMatrix {
    
    private int size;       // The size of the matrix
/**
 * Constructor of the Matrix of certain length
 * @param length		The dimension of the matrix
 */
    public UptriangularMatrix(int length) {


        double computeSize = Math.sqrt(2 * length + 0.25) - 0.5;
        if (computeSize == (int)computeSize) {
            this.size = (int)computeSize + 1;
        } else {
            throw new IllegalArgumentException("Inappropriate length.");
        }
    }
    
/**
 * Finds the corresponding cell of a given index in the matrix 
 * @param index		The index in the matrix
 * @return cell		The cell the index points to
 */
    
    public Cell getPosition(int index) {

        int remaining = index;
        int row = 0;
        int availableLength = this.size - 1;

        while (remaining > availableLength) {
            remaining -= availableLength;
            row++;
            availableLength = this.size - row - 1;
        }
        return new Cell(row, remaining + row);
    }
    
    
    public int getSize() {
        
        return this.size;
    }
  /**
   * Cell objects in the matrix
   * The position of the cell in the matrix is represented by the row and col in Cell  
   * @author Vivian
   *
   */
    public class Cell {
        
        public int row;
        public int col;
        
        public Cell(int row, int col) {
            
            this.row = row;
            this.col = col;
        }
        
        public int getRow()
        {
        	return row;
        }
        
        public int getcol()
        {
        	return col;
        }
        
        public String toString() {
            
            return "[" + row + ", " + col + "]";
        }
    }
    
//    public static void main(String[] args) {
//        
//        // The matrix will be:
//        //   0  1  2  3  4 
//        //   0  0  5  6  7
//        //   0  0  0  8  9 
//        //   0  0  0  0  10
//        //   0  0  0  0  0
//        UptriangularMatrix matrix = new UptriangularMatrix(10);
//        
//        System.out.println("Size: " + matrix.getSize());
//        
//        for (int i = 1; i <= 10; i++) {
//            UptriangularMatrix.Cell cell = matrix.getPosition(i);
//            System.out.println("index: " + i + "\t" + cell);
//        }
//
//        int range = 5 * 4 / 2;
//        matrix = new UptriangularMatrix(range);
//
//        for (int i = range; i > 0; i--) {
//            System.out.println("i: " + i);
//            UptriangularMatrix.Cell cell = matrix.getPosition(i);
//            System.out.println("index: " + i + "\t" + cell);
//        }
//    }
}
