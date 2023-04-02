import java.util.Arrays;

public class var_test {
    private double[] random_array(int n){
        double[] n_array=new double[n];
        for(int i=0;i<n;i++){
            n_array[i]=Math.random();
        }
        return n_array;
    }

    private double[] get_regions(double[] n_array){
        Arrays.sort(n_array);
        double[] regions = new double[2];
        int num=n_array.length;
        int up_site= (int) Math.floor(num*0.975);   //向下取整
        int down_site=(int) Math.floor(num*0.025);
        regions[1]=n_array[up_site];
        System.out.println("up: "+regions[1]);
        regions[0]=n_array[down_site];
        System.out.println("down: "+regions[0]);
        return regions;
    }

    private boolean is_cross(double[] array_a,double[] array_b){
        double[] regions_a = get_regions(array_a);
        double[] regions_b = get_regions(array_b);
        double start_max=Math.max(regions_a[0],regions_b[0]);
        double end_min=Math.min(regions_a[1],regions_b[1]);
        return (start_max<=end_min);
    }

    public static void main(String[] args) {
        var_test test = new var_test();
        double[] arr = test.random_array(10);
        double[] brr = test.random_array(10);
        if(test.is_cross(arr,brr)){
            System.out.println("They are cross!");
        }else {
            System.out.println("They are not cross!");
        }
    }
}
