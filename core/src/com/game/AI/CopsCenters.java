package com.game.AI;


import java.lang.Math;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import com.badlogic.gdx.math.Vector2;
import com.game.Board.Agent;
import com.game.Board.Board;
import com.game.Board.Guard;
import com.game.Board.MapDivider;
import com.game.States.MainState;

import java.awt.Point;
import java.util.Collections;
import java.util.Map;


    /**
     * When calling the constructor of the class all the x-y location of the centers of the areas and the staring location of the guards are put in arraylist.
     * The algorithm returns an arraylist of Point2D.Float where for each element of the array (that represents a cop, in the order with which it got them) contains the location of the nearest untaken area-center
     *
     */

public class CopsCenters {

    private MapDivider mp;
    private MainState ms;
    private ArrayList<Point2D.Float> centres;
    private ArrayList<Point2D.Float> guardsLoc;
    //private ArrayList<Point2D.Float> guardCenter;
    private Point2D.Float guardCenter[];
    private ArrayList<Agent> guards;
    private ArrayList<ArrayList<Double>> distances;
    private ArrayList<Double> dist;
    private ArrayList<Double> standDev;
    private ArrayList<Integer> ordered;
    private ArrayList<Integer> coupled;
    private Board board;
    private Guard guard;
    private int pos;
    private double maxSD,minDist;

    public CopsCenters (ArrayList<Agent> guards) {

        this.guards = guards;
        MapDivider mp = new MapDivider(guards.size());
        centres = mp.getCentres();
        guardsLoc = new ArrayList<Point2D.Float>();
        standDev = new ArrayList<Double>();
        ordered = new ArrayList<Integer>();
        distances = new ArrayList<ArrayList<Double>>();
        //guardCenter = new ArrayList<Point2D.Float>(guards.size());
        guardCenter = new Point2D.Float[guards.size()];
        coupled = new ArrayList<Integer>(guards.size());

        for (int i=0;i<centres.size();i++) {
            Point2D.Float tempLoc = new Point2D.Float(guards.get(i).getX(),guards.get(i).getY());
            guardsLoc.add(tempLoc);
            //guardsLoc.get(i).setLocation(guards.get(i).getX(),guards.get(i).getY());
        }

        for(int i = 0; i < guards.size(); i++){
            coupled.add(null);
        }
    }

    // This method computes the distance between each guard and all the possible area-centers. It also computes the standard deviation for the distances of each cop
    public void setDistances () {
        for (int i=0;i<centres.size();i++) {
            distances.add(computeEuclideanDist(i));
            //distances.set(i,computeEuclideanDist(i));
        }
        for (int i=0;i<centres.size();i++) {
            standDev.add(calculateSD(distances.get(i)));
            //standDev.set(i,calculateSD(distances.get(i)));
        }
    }

    // Compute euclidean distance between a guard and all the area-centers
    public ArrayList<Double> computeEuclideanDist(int i) {
        dist = new ArrayList<Double>();
        for (int j=0;j<centres.size();j++) {
            dist.add(Math.sqrt((Math.pow(centres.get(j).getX()-guardsLoc.get(i).getX(),2))+(Math.pow(centres.get(j).getY()-guardsLoc.get(i).getY(),2))));
            //dist.set(j,Math.sqrt((Math.pow(centres.get(j).getX()-guardsLoc.get(i).getX(),2))+(Math.pow(centres.get(j).getY()-guardsLoc.get(i).getY(),2))));
        }
        return dist;
    }

    // computes Standard Deviation for each guard
    public double calculateSD(ArrayList<Double> span)
    {
        double sum = 0.0;
        double standardDeviation = 0.0;
        int length = span.size();
        int i;

        for (i=0;i<length;i++) {
            sum += span.get(i);
        }

        double mean = sum/length;
        for (i=0;i<length;i++) {
            standardDeviation += Math.pow(span.get(i) - mean, 2);
        }
        return Math.sqrt(standardDeviation/length);
    }

    // The method orders the guards by highest standard deviation w.r.t. distances. The guard with highest standard deviation will be the first to receive the nearest center
    public void order () {
        for (int j=0;j<centres.size();j++) {
            maxSD=0;
            pos=0;
            for (int i=0;i<centres.size();i++)  {
                if (maxSD<standDev.get(i)) {
                    maxSD=standDev.get(i);
                    pos=i;
                }
            }
            standDev.set(pos,-standDev.get(pos));
            ordered.add(pos);
        }
    }

    // this is the method to call to get the total output of the algorithm
    public Point2D.Float[] getCenters () {
        setDistances();
        order();
        for (int i=0;i<centres.size();i++) {
            //System.out.println("centres size: " + centres.size());
            //System.out.println("ordered.get(i): " + ordered.get(i));
            coupled.set(ordered.get(i),findMin(distances.get(ordered.get(i))));
            //System.out.println("ok");
            //System.out.println("i: " + i);
        }
        //coupled.set(0, findMin(distances.get(ordered.get(0))));
        //coupled.set(1, findMin(distances.get(ordered.get(1))));


        for (int i=0;i<centres.size();i++) {

            //guardCenter.set(coupled.get(i),centres.get(i));
            guardCenter[coupled.get(i)] = centres.get(i);

        }
        return guardCenter;
    }

    //finds the 4 corner points of the area associated with each center
    public ArrayList<ArrayList<Point2D.Float>> getAreas(Point2D.Float guardCenter[]) {

        ArrayList<ArrayList<Point2D.Float>> areas = new ArrayList<ArrayList<Point2D.Float>>();

            //if even nr of guards, map is horizontally split
            if(guards.size() % 2 == 0) {
                if (guards.size() % 4 == 0) {
                    //bottom half of areas
                    for (int j = 0; j < guards.size() / 2; j++) {
                        Point2D.Float corner1 = new Point2D.Float(j * Board.BOARD_WIDTH * 5 / (guards.size()/4) / 2, 0 * Board.BOARD_HEIGHT * 5 / (guards.size()/4) / 2);
                        Point2D.Float corner2 = new Point2D.Float(j * Board.BOARD_WIDTH * 5 / (guards.size()/4) / 2, 1 * Board.BOARD_HEIGHT * 5 / (guards.size()/4) / 2);
                        Point2D.Float corner3 = new Point2D.Float((j + 1) * Board.BOARD_WIDTH * 5 / (guards.size()/4) / 2, 1 * Board.BOARD_HEIGHT * 5 / (guards.size()/4) / 2);
                        Point2D.Float corner4 = new Point2D.Float((j + 1) * Board.BOARD_WIDTH * 5 / (guards.size()/4) / 2, 0 * Board.BOARD_HEIGHT * 5 / (guards.size()/4) / 2);

                        ArrayList<Point2D.Float> corners = new ArrayList<Point2D.Float>();
                        corners.add(corner1);
                        corners.add(corner2);
                        corners.add(corner3);
                        corners.add(corner4);
                        areas.add(corners);
                    }
                    //top half of areas
                    for (int j = 0; j < guards.size() / 2; j++) {
                        Point2D.Float corner1 = new Point2D.Float(j * Board.BOARD_WIDTH * 5 / (guards.size()/4) / 2, 1 * Board.BOARD_HEIGHT * 5 / (guards.size()/4) / 2);
                        Point2D.Float corner2 = new Point2D.Float(j * Board.BOARD_WIDTH * 5 / (guards.size()/4) / 2, 2 * Board.BOARD_HEIGHT * 5 / (guards.size()/4) / 2);
                        Point2D.Float corner3 = new Point2D.Float((j + 1) * Board.BOARD_WIDTH * 5 / (guards.size()/4) / 2, 2 * Board.BOARD_HEIGHT * 5 / (guards.size()/4) / 2);
                        Point2D.Float corner4 = new Point2D.Float((j + 1) * Board.BOARD_WIDTH * 5 / (guards.size()/4) / 2, 1 * Board.BOARD_HEIGHT * 5 / (guards.size()/4) / 2);

                        ArrayList<Point2D.Float> corners = new ArrayList<Point2D.Float>();
                        corners.add(corner1);
                        corners.add(corner2);
                        corners.add(corner3);
                        corners.add(corner4);
                        areas.add(corners);
                    }
                    //if divisible by 2 but not 4:
                } else {
                    //bottom half of areas
                    for (int j = 0; j < guards.size() / 2; j++) {
                        Point2D.Float corner1 = new Point2D.Float(j * Board.BOARD_WIDTH * 5, 0 * Board.BOARD_HEIGHT * 5 / 2);
                        Point2D.Float corner2 = new Point2D.Float(j * Board.BOARD_WIDTH * 5, 1 * Board.BOARD_HEIGHT * 5 / 2);
                        Point2D.Float corner3 = new Point2D.Float((j + 1) * Board.BOARD_WIDTH * 5, 1 * Board.BOARD_HEIGHT * 5 / 2);
                        Point2D.Float corner4 = new Point2D.Float((j + 1) * Board.BOARD_WIDTH * 5, 0 * Board.BOARD_HEIGHT * 5 / 2);

                        ArrayList<Point2D.Float> corners = new ArrayList<Point2D.Float>();
                        corners.add(corner1);
                        corners.add(corner2);
                        corners.add(corner3);
                        corners.add(corner4);
                        areas.add(corners);
                    }
                    //top half of areas
                    for (int j = 0; j < guards.size() / 2; j++) {
                        Point2D.Float corner1 = new Point2D.Float(j * Board.BOARD_WIDTH * 5, 1 * Board.BOARD_HEIGHT * 5 / 2);
                        Point2D.Float corner2 = new Point2D.Float(j * Board.BOARD_WIDTH * 5, 2 * Board.BOARD_HEIGHT * 5 / 2);
                        Point2D.Float corner3 = new Point2D.Float((j + 1) * Board.BOARD_WIDTH * 5, 2 * Board.BOARD_HEIGHT * 5 / 2);
                        Point2D.Float corner4 = new Point2D.Float((j + 1) * Board.BOARD_WIDTH * 5, 1 * Board.BOARD_HEIGHT * 5 / 2);

                        ArrayList<Point2D.Float> corners = new ArrayList<Point2D.Float>();
                        corners.add(corner1);
                        corners.add(corner2);
                        corners.add(corner3);
                        corners.add(corner4);
                        areas.add(corners);
                    }
                }
            }
            else{
                for (int j = 0; j < guards.size(); j++) {
                    Point2D.Float corner1 = new Point2D.Float(j * Board.BOARD_WIDTH * 5 / guards.size(), 0 * Board.BOARD_HEIGHT * 5);
                    Point2D.Float corner2 = new Point2D.Float(j * Board.BOARD_WIDTH * 5 / guards.size(), 1 * Board.BOARD_HEIGHT * 5);
                    Point2D.Float corner3 = new Point2D.Float((j + 1) * Board.BOARD_WIDTH * 5 / guards.size(), 1 * Board.BOARD_HEIGHT * 5);
                    Point2D.Float corner4 = new Point2D.Float((j + 1) * Board.BOARD_WIDTH * 5 / guards.size(), 0 * Board.BOARD_HEIGHT * 5);

                    ArrayList<Point2D.Float> corners = new ArrayList<Point2D.Float>();
                    corners.add(corner1);
                    corners.add(corner2);
                    corners.add(corner3);
                    corners.add(corner4);
                    areas.add(corners);
                }
            }

        //find correct center for each list of cornerpoints

        ArrayList<ArrayList<Point2D.Float>> orderedAreas = new ArrayList<ArrayList<Point2D.Float>>();

        for(int i = 0; i < guardCenter.length; i++){
            for(int j = 0; j < areas.size(); j++){
                    if (guardCenter[i].x > areas.get(j).get(0).x && guardCenter[i].x < areas.get(j).get(2).x
                        && guardCenter[i].y > areas.get(j).get(0).y && guardCenter[i].y < areas.get(j).get(2).y) {
                        orderedAreas.add(areas.get(j));
                    }
                }
        }

        return orderedAreas;
    }

    //return position in arraylist of nearest untaken center
    public int findMin (ArrayList<Double> dists) {
        minDist=1000000;
        for (int i=0;i<centres.size();i++)  {
            if(!coupled.contains(i)) {
                if (minDist>dists.get(i)) {
                    minDist=dists.get(i);
                    pos=i;
                }
            }
        }
        return pos;
    }

    public MainState getMs() {
        return ms;
    }

    public ArrayList<Agent> getGuards() {
        return guards;
    }
}

