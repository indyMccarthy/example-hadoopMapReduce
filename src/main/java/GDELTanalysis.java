import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import java.io.IOException;
import java.util.*;

import static java.lang.Integer.parseInt;

class MyMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

    private final static IntWritable one = new IntWritable(1);
    private Text word = new Text();

    public void map(LongWritable key, Text value, Context context) throws IOException,InterruptedException {
        /*String line = value.toString();
        StringTokenizer tokenizer = new StringTokenizer(line);
        while (tokenizer.hasMoreTokens()) {
            String tmp = tokenizer.nextToken();
            if(tmp.charAt(0)=='M'|| tmp.charAt(0)=='m') {
                System.out.println(tmp);
                word.set(tmp);
                context.write(word, one);
            }
        }*/
        //GDELT Mapper
        String line = value.toString();
        String[] s = line.split("\t");
        Text countryCode = new Text();
        IntWritable Nmention = new IntWritable();
        if(s[7].matches("[A-Z]{3}")) {
            countryCode.set(s[7]);
            Nmention.set(parseInt(s[32]));
            context.write(countryCode, Nmention);
        }
    }
}

class MyReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
    HashMap<String,Integer> m_HM = new HashMap<String, Integer>();

    public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
        int sum = 0;
        for (IntWritable val : values) {
            sum += val.get();
        }
        m_HM.put(key.toString(),sum);
    }

    public void cleanup(Context context)throws IOException, InterruptedException {
        ArrayList<Integer> m_AL = new ArrayList<Integer>();
        for (Integer i : m_HM.values()){
            m_AL.add(i);
        }

        Set val = new HashSet();
        val.addAll(m_AL);
        m_AL.clear();
        m_AL.addAll(val);
        Collections.sort(m_AL);
        Collections.reverse(m_AL);
        m_AL.subList(10,m_AL.size()).clear();

        Text t = new Text();
        IntWritable ii = new IntWritable();

        for (Integer i : m_AL){
            for (String s : m_HM.keySet()){
                if(m_HM.get(s).equals(i)){
                    t.set(s);
                    ii.set(i);
                    context.write(t,ii);
                }
            }
        }
        //context.write(key, new IntWritable(sum));
    }
}

public class GDELTanalysis {
    public static void main(String[] args) throws Exception {

        long start = System.currentTimeMillis();
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "GDELT id03");
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        job.setMapperClass(MyMapper.class);
        //job.setCombinerClass(MyReducer.class);
        job.setReducerClass(MyReducer.class);
        job.setNumReduceTasks(1);// run only one reduce task
        job.setJarByClass(GDELTanalysis.class);
        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);
        FileInputFormat.addInputPath(job, new Path("./dataset/gdeltmini/*"));
        //FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path("./dataset/gdeltmini/Result"));
        //FileOutputFormat.setOutputPath(job, new Path(args[1]));
        job.waitForCompletion(true);

        long end = System.currentTimeMillis();
        System.out.println("PROCESSING TIME :"+ (double) ((end - start)/1000)+ "sec");
    }
}