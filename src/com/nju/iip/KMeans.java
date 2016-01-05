package com.nju.iip;

import java.util.*;

/**
 * @author gaoyang 2016-01-05
 */
public class KMeans {
		
    /**
     * 计算两个向量 v1和 v2之间的 欧氏距离
     * @param v1
     * @param v2
     * @return
     */
    protected double euclideanDistance(double[] v1, double[] v2) {
        double sum = 0;
        for (int i = 0; i < v1.length; i++) {
            sum += (v1[i] - v2[i]) * (v1[i] - v2[i]);
        }
        return Math.sqrt(sum);
    }
    
    /**
     * 计算两个向量 v1和 v2之间的余弦距离
     * @param v1
     * @param v2
     * @return
     */
    protected double cosineDistance(double[] v1, double[] v2) {
        double result = 0;
        result = pointMulti(v1, v2) / sqrtMulti(v1, v2);  
        return 1 - result;
    }

	/**
	 * 计算向量v1 和v2的点积,结果用于计算余弦距离
	 * @param v1
	 * @param v2
	 * @return
	 */
	private double pointMulti(double[] v1, double[] v2) {
		double result = 0;  
        for (int i = 0; i < v1.length; i++) {  
            result += v1[i] * v2[i];  
        }  
        return result; 
	}   
    
    /**
     * 计算向量v1 和v2 模的乘积,结果用于计算余弦距离
     * @param v1
     * @param v2
     * @return
     */
    private double sqrtMulti(double[] v1, double[] v2) {
    	double result1 = 0;  
        for (int i = 0; i < v1.length; i++) {  
            result1 += v1[i] * v1[i];  
        }  
        double result2 = 0;  
        for (int i = 0; i < v2.length; i++) {  
            result2 += v2[i] * v2[i];  
        } 
        return Math.sqrt(result1) * Math.sqrt(result2); 
	}
  
    /**
     * 要进行聚类的向量
     */
    protected double[][] vectors;
       
    /**
     * 聚类的个数
     */
    protected int k;
      
    /**
     * 每一个向量的长度
     */
    protected int vectorLen = -1;
      
    /**
     * 每一个类的类标 label
     */
    public int[] labels;
    
    
    /**
     *  生成的中心点
     */
    public double[][] centers;
    
    /**
     * 某一标签下的向量数目
     */
    int[] labelCount;
    
    /**
     *  表示某标签下所有向量的和
     */
    double[][] vectorSum;
          
    /**
     * 构造方法
     * @param vectors 待聚类的数据
     * @param k 聚类的个数
     */
    public KMeans(double[][] vectors, int k) {   	
    	
        this.vectors = vectors;
        this.k = k;
        this.vectorLen = vectors[0].length;
        this.centers = new double[k][];

        this.labelCount = new int[k];
        this.vectorSum = new double[k][];
        this.labels = new int[vectors.length];
        
        for (int i = 0; i < k; i++) {
            vectorSum[i] = new double[vectorLen];
        }
        initCenters();
    }
    
    /**
     * 计算某一向量的新label,离哪个中心点近,label
     * @param vector
     * @return
     */
    public int computeLabel(double[] vector) {
        double minDis = Double.MAX_VALUE;
        int label = -1;
        for (int i = 0; i < centers.length; i++) {
            double dis = euclideanDistance(vector, centers[i]);
            if (dis < minDis) {
                label = i;
                minDis = dis;
            }
        }
        return label;
    }

    /**
     * 随机初始化k个中心点
     */
    public void initCenters(){
        centers[0] = vectors[0].clone();
        int centersSize = 1;
        Random random = new Random();
        
        for(int i = 1;i < k; i++){ 
            double[] p = new double[vectors.length];
            double sum = 0;
            for(int row = 0; row < vectors.length; row++){
                double disSum = 0;
                for(int j = 0; j < centersSize; j++){
                    disSum += euclideanDistance(centers[j],vectors[row]);
                }
                sum += disSum;
                p[row] = sum;
            }
            for(int row = 0; row < vectors.length; row++){
                p[row] = p[row]/sum;
            }
            double r;
            while((r = random.nextDouble()) == 0);
            for(int row = 0; row < vectors.length; row++){
                if(p[row] >= r){
                    centers[centersSize] = vectors[row].clone();
                    centersSize++;
                    break;
                }
            }         
        }
        
    }
    
    /**
     * 达到迭代次数,或者聚类中心已不再变化,停止
     * @param round 迭代次数
     */
    public void start(int round) {
        int count = 0;
        while (count++ < round && !once()) ;
    }


    /**
     * 一次迭代 return true 表示聚类中心已经不再变化
     * @return
     */
    public boolean once() {
        System.out.println("start once");
        for (int i = 0; i < k; i++) {
            labelCount[i] = 0;
            for (int j = 0; j < vectorLen; j++) {
                vectorSum[i][j] = 0;
            }
        }

        for (int row = 0; row < vectors.length; row++) {
            double[] vector = vectors[row];
            int label = computeLabel(vector);
            labels[row] = label;
            labelCount[label]++;
            for (int col = 0; col < vectorLen; col++) {
                vectorSum[label][col] += vector[col];
            }
        }
        boolean hasComplete = true;
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < vectorLen; j++) {
                double ave = (double) vectorSum[i][j] / labelCount[i];
                if (ave != centers[i][j]) {
                    hasComplete = false;
                }
                centers[i][j] = ave;
            }
        }
        return hasComplete;
    }

    public void showResult(){
        for(int i = 0; i < this.vectors.length; i++){      	
            System.out.print("label:"+this.labels[i]+"\t");           
            for(int j = 0; j < this.vectorLen; j++){
                System.out.print(this.vectors[i][j]+",");
            }
            System.out.println();
        }
    }
    
    public static void main(String[] args) {
        double[][] vectors = new double[][]{
                {1, 2},
                {1, 1.2},
                {4, 1},
                {100, 101},
                {99, 98},
                {97, 87}
        };
                
        KMeans kMeans = new KMeans(vectors, 2);
        kMeans.start(3);
        kMeans.showResult();
    }


}
