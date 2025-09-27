import kareltherobot.*;
import java.awt.Color;
import java.util.concurrent.Semaphore;
class MyRobot extends Robot
{
   int ID;
   public MyRobot(int ID, int Street, int Avenue, Direction direction, int beeps, Color color)
   {
       super(Street, Avenue, direction, beeps, color);
       this.ID = ID;
       World.setupThread(this);
   }
   public void turnRight(){turnLeft(); turnLeft(); turnLeft(); }
   public void moveLeft(){move(); turnLeft();}
   public void leftMove(){turnLeft(); move();}
   public void moveRight(){move(); turnRight();}
   public void rightMove(){turnRight(); move();}
}
 
class MyThread extends Thread
{
   MyRobot robot;
   int street;
   int avenue;
   private volatile boolean group;
   int[][] localMovements;
   public MyThread(int street, int avenue, Directions.Direction direction, boolean group, Color color)
   {
       this.robot = new MyRobot(Shared.ID++, street, avenue, direction, 0, color);
       this.street = street;
       this.avenue = avenue;
       this.group = group;
       try {  
           Shared.cellLocks[street][avenue].acquire();
       }catch (InterruptedException e) {
               e.printStackTrace();
       }
   }
   public void setGroup(boolean newGroup) {
       this.group = newGroup;
   }
   public boolean isGroup() {
       return group;
   }
   public void go(Runnable action){
       try {  
           int streetN = street;
           int avenueN = avenue;
           if (robot.facingEast()) avenueN = avenue + 1;
           else if (robot.facingWest()) avenueN = avenue - 1;
           else if (robot.facingNorth()) streetN = street + 1;
           else if (robot.facingSouth()) streetN = street - 1;
           Shared.cellLocks[streetN][avenueN].acquire();   // lock cell
           action.run();
           Shared.cellLocks[street][avenue].release();
           street = streetN;
           avenue = avenueN;
       } catch (InterruptedException e) {
           e.printStackTrace();
       }
   }
   @Override
   public void run() {
       while(true){
           if (group) {
               localMovements = Shared.group1;
           } else {
               localMovements = Shared.group0;
           }
           while(true){
               int movement = localMovements[20-street][avenue-1];
               if(movement == 1) {go(robot::move);}
               else if(movement == 2) {go(robot::moveLeft);}
               else if(movement == 3) {go(robot::moveRight);}
               else if(movement == 4){
                   if(Shared.A0 < 4){Shared.A0++; go(robot::move);}
                   else{go(robot::moveLeft);}
               }
               else if(movement == 5){
                   try{
                       Shared.As.acquire();
                       if(group){go(robot::moveRight);}
                       else{go(robot::move); Shared.A0--;}
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }
               }
               else if(movement == 6){
                   Shared.As.release();
                   go(robot::move);
               }
               else if(movement == 7){
                   try{
                       Shared.Bs.acquire();
                       if(group){go(robot::moveRight);}
                       else{go(robot::move);}
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }
               }
               else if(movement == 8){
                   Shared.Bs.release();
                   go(robot::move);
               }
               else if(movement == 9){
                   try{
                       Shared.Cs.acquire();
                       if(group){go(robot::moveRight); Shared.D--;}
                       else{go(robot::move);}
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }
               }
               else if(movement == 10){
                   Shared.Cs.release();
                   go(robot::move);
               }
               else if(movement == 11){
                    int i;
                    for(i = 1;i <= 4;i++){
                        if (robot.nextToABeeper()) {
                            robot.pickBeeper();
                        } else {
                            i--;
                        }
                    }
                    if(Shared.D < 6){
                        Shared.D++;
                        go(robot::move);
                        go(robot::moveLeft);
                    }
                    else{
                        go(robot::moveRight);
                        go(robot::move);
                    }
               }
               else if(movement == 12){
                   Shared.D++;
                   go(robot::move);
               }
               else if(movement == 13){
                   go(robot::moveLeft);
                   Shared.D--;
                   go(robot::move);
                   go(robot::move);
               }
               else if(movement == 14){
                int i;
                for(i = 1;i <= 4;i++){
                    if (robot.nextToABeeper()) {
                        robot.pickBeeper();
                    } else {
                        i--;
                    }
                }
                go(robot::move);
               }
               else if(movement == 15){
                    while(robot.anyBeepersInBeeperBag()) {
                        robot.putBeeper();
                    }
                    if (group) {
                        go(robot::moveRight);
                    } else {
                        go(robot::move);
                    }
               }
               else if(movement == -1){
                   go(robot::move);
                   group = !group;
                   break;
               }
           }
       }
   }
}
 
class Shared {
   public static int ID = 0;
   public static int A0, D;
   public static Semaphore As = new Semaphore(1);
   public static Semaphore Bs = new Semaphore(1);
   public static Semaphore Cs = new Semaphore(1);

   public static final int calles = 20;
   public static final int avenidas = 30;
   
 
   // Per-cell semaphores (1 robot per cell)
   public static final Semaphore[][] cellLocks = new Semaphore[calles+1][avenidas+1];
   
   static {
       for (int i = 0; i <= calles; i++) {
           for (int j = 0; j <= avenidas; j++) {
               cellLocks[i][j] = new Semaphore(1, true);
           }
       }
   }
   // Matrix of movements
   // 1: move
   // 2: moveLeft
   // 3: moveRight
   public static final int[][] group0 = { //blue = 0
      //1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0
       {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, // 20
       {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, // 19
       {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, // 18
       {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, // 17
       {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 2}, // 16
       {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 1, 1, 1, 1, 1, 1}, // 15
       {0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 3, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 3, 1, 1}, // 14
       {0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 2, 1, 1, 1, 1, 3, -1}, // 13
       {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 2, 3, 15}, // 12
       {0, 0, 0, 0, 0, 0, 0, 1, 3, 1, 1, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 10}, // 11
       {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 1, 2, 1, 1, 0, 0, 0, 1, 1, 12, 1, 1, 1, 1, 1, 1, 13, 1}, // 10
       {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 9
       {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 8
       {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 7
       {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 2, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 6
       {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1, 1, 1, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 5
       {2, 2, 1, 1, 1, 1, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9}, // 4
       {1, 1, 1, 1, 1, 3, 3, 2, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 3
       {2, 2, 1, 1, 1, 1, 1, 3, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 2
       {1, 1, 1, 1, 1, 1, 14, 1, 1, 4, 1, 1, 1, 1, 5, 1, 1, 1, 1, 1, 1, 6, 1, 1, 7, 1, 1, 1, 2, 8}, // 1
      //1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0
   };
   public static final int[][] group1 = { //green = 1
      //1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0
       {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, // 20
       {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, // 19
       {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, // 18
       {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, // 17
       {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 3, 2}, // 16
       {1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 0, 2, 2, 1, 1, 1, 1, 1, 2}, // 15
       {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 1, 1, 1, 3, 1, 1}, // 14
       {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 2, 1, 1, 1, 1, 3, 1}, // 13
       {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 11, 0, 0, 0, 0, 2, 3, 0}, // 12
       {2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 3, 1, 0, 0, 0, 0, 0, 0, 0, 0}, // 11
       {1, 1, 1, 1, 1, 1, 1, 1, 3, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 9, 1}, // 10
       {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 9
       {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 8
       {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 7
       {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3}, // 6
       {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 10, 2}, // 5
       {2, 2, 1, 1, 1, 1, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0}, // 4
       {1, 1, 1, 1, 1, 3, 2, 2, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0}, // 3
       {2, 2, 1, 1, 1, 1, 1, -1, 15, 1, 1, 1, 1, 1, 1, 6, 0, 0, 0, 0, 5, 2, 1, 1, 1, 8, 0, 0, 7, 0}, // 2
       {1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 2, 3, 1, 1, 1, 1, 0, 0, 0, 0, 2, 3, 1, 1, 0}, // 1
      //1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0
   };
   
}
class Main{
   public static void main(String [] args){
       // Leer mundo y poner velocidad
       World.readWorld("Entrega2.kwld");
       World.setVisible(true);
       World.setDelay(5);
       // Crear un arreglo para 56 robots
       MyThread[] threads = new MyThread[56];
       // ROBOTS AZULES (primeros 28) - Parqueadero azul
       threads[0] = new MyThread(4, 7, Directions.West, false, Color.BLUE);
       threads[1] = new MyThread(4, 6, Directions.West, false, Color.BLUE);
       threads[2] = new MyThread(4, 5, Directions.West, false, Color.BLUE);
       threads[3] = new MyThread(4, 4, Directions.West, false, Color.BLUE);
       threads[4] = new MyThread(4, 3, Directions.West, false, Color.BLUE);
       threads[5] = new MyThread(4, 2, Directions.West, false, Color.BLUE);
       threads[6] = new MyThread(4, 1, Directions.South, false, Color.BLUE);
       threads[7] = new MyThread(3, 1, Directions.East, false, Color.BLUE);
       threads[8] = new MyThread(3, 2, Directions.East, false, Color.BLUE);
       threads[9] = new MyThread(3, 3, Directions.East, false, Color.BLUE);
       threads[10] = new MyThread(3, 4, Directions.East, false, Color.BLUE);
       threads[11] = new MyThread(3, 5, Directions.East, false, Color.BLUE);
       threads[12] = new MyThread(3, 6, Directions.East, false, Color.BLUE);
       threads[13] = new MyThread(3, 7, Directions.South, false, Color.BLUE);
       threads[14] = new MyThread(2, 7, Directions.West, false, Color.BLUE);
       threads[15] = new MyThread(2, 6, Directions.West, false, Color.BLUE);
       threads[16] = new MyThread(2, 5, Directions.West, false, Color.BLUE);
       threads[17] = new MyThread(2, 4, Directions.West, false, Color.BLUE);
       threads[18] = new MyThread(2, 3, Directions.West, false, Color.BLUE);
       threads[19] = new MyThread(2, 2, Directions.West, false, Color.BLUE);
       threads[20] = new MyThread(2, 1, Directions.South, false, Color.BLUE);
       threads[21] = new MyThread(1, 1, Directions.East, false, Color.BLUE);
       threads[22] = new MyThread(1, 2, Directions.East, false, Color.BLUE);
       threads[23] = new MyThread(1, 3, Directions.East, false, Color.BLUE);
       threads[24] = new MyThread(1, 4, Directions.East, false, Color.BLUE);
       threads[25] = new MyThread(1, 5, Directions.East, false, Color.BLUE);
       threads[26] = new MyThread(1, 6, Directions.East, false, Color.BLUE);
       threads[27] = new MyThread(1, 7, Directions.East, false, Color.BLUE);
       // ROBOTS VERDES (últimos 28) - Parqueadero verde
       threads[28] = new MyThread(12, 23, Directions.South, true, Color.GREEN);
       threads[29] = new MyThread(13, 23, Directions.South, true, Color.GREEN);
       threads[30] = new MyThread(13, 24, Directions.West, true, Color.GREEN);
       threads[31] = new MyThread(13, 25, Directions.West, true, Color.GREEN);
       threads[32] = new MyThread(13, 26, Directions.West, true, Color.GREEN);
       threads[33] = new MyThread(13, 27, Directions.West, true, Color.GREEN);
       threads[34] = new MyThread(13, 28, Directions.West, true, Color.GREEN);
       threads[35] = new MyThread(12, 28, Directions.North, true, Color.GREEN);
       threads[36] = new MyThread(12, 29, Directions.West, true, Color.GREEN);
       threads[37] = new MyThread(13, 29, Directions.South, true, Color.GREEN);
       threads[38] = new MyThread(14, 29, Directions.South, true, Color.GREEN);
       threads[39] = new MyThread(14, 28, Directions.East, true, Color.GREEN);
       threads[40] = new MyThread(14, 27, Directions.East, true, Color.GREEN);
       threads[41] = new MyThread(14, 26, Directions.East, true, Color.GREEN);
       threads[42] = new MyThread(14, 25, Directions.East, true, Color.GREEN);
       threads[43] = new MyThread(14, 24, Directions.East, true, Color.GREEN);
       threads[44] = new MyThread(14, 23, Directions.East, true, Color.GREEN);
       threads[45] = new MyThread(15, 23, Directions.South, true, Color.GREEN);
       threads[46] = new MyThread(15, 24, Directions.West, true, Color.GREEN);
       threads[47] = new MyThread(15, 25, Directions.West, true, Color.GREEN);
       threads[48] = new MyThread(15, 26, Directions.West, true, Color.GREEN);
       threads[49] = new MyThread(15, 27, Directions.West, true, Color.GREEN);
       threads[50] = new MyThread(15, 28, Directions.West, true, Color.GREEN);
       threads[51] = new MyThread(15, 29, Directions.West, true, Color.GREEN);
       threads[52] = new MyThread(16, 29, Directions.South, true, Color.GREEN);
       threads[53] = new MyThread(16, 30, Directions.West, true, Color.GREEN);
       threads[54] = new MyThread(15, 30, Directions.North, true, Color.GREEN);
       threads[55] = new MyThread(14, 30, Directions.North, true, Color.GREEN);

        World.setDelay(5);
       for(int i = 0; i < 56; i++) threads[i].start();
   }
}