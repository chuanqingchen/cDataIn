package com.trendy.bigdata.cutPoint;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.ArrayList;
import java.util.List;

public class Sort {
    public static void main(String[] args) throws IOException {
        Sort s = new Sort();
        s.splitByChannel("C:\\Users\\test\\Desktop\\OCH_sku.csv",
                10, "E:\\CutPoint\\OCH_sku_");
    }

    /**
     * 使用IO流切分指定文件
     */
    public List<File> splitByStream(String file, int piece, String outputDirectiry) throws IOException {
        List<File> result = new ArrayList<File>();
        List<Point> list = blocking(new File(file), piece);
        for (int i = 0; i < list.size(); i++) {
            File outputFile = new File(outputDirectiry + i + "_byStream.txt");
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outputFile));
            in.skip(list.get(i).getSkipSize());
            int index = 0;
            while (index < list.get(i).getLength()) {
                out.write(in.read());
                index++;
            }
            out.flush();
            out.close();
            in.close();
            result.add(outputFile);
        }
        return result;
    }

    /**
     * 使用內存映射文件切分指定文件
     */
    public List<File> splitByMappedByteBuffer(String file, int piece, String outputDirectiry) throws IOException {
        List<File> result = new ArrayList<File>();
        List<Point> list = blocking(new File(file), piece);
        for (int i = 0; i < list.size(); i++) {
            File outputFile = new File(outputDirectiry + i + "_byMappedByteBuffer.txt");
            FileChannel in = new RandomAccessFile(file, "r").getChannel();
            FileChannel out = new RandomAccessFile(outputFile, "rw").getChannel();
            MappedByteBuffer outBuffer = out.map(MapMode.READ_WRITE, 0, list.get(i).length);
            MappedByteBuffer inBuffer = in.map(MapMode.READ_ONLY, list.get(i).getSkipSize(), list.get(i).getLength());
            outBuffer.put(inBuffer);
            outBuffer.force();
            in.close();
            out.close();
            result.add(outputFile);
        }
        return result;
    }

    /**
     * 使用通道切分指定文件
     */
    public List<File> splitByChannel(String file, int piece, String outputDirectiry) throws IOException {
        List<File> result = new ArrayList<File>();
        List<Point> list = blocking(new File(file), piece);
        for (int i = 0; i < list.size(); i++) {
            File outputFile = new File(outputDirectiry + i + "_byChannel.txt");
            FileChannel in = new FileInputStream(file).getChannel();
            FileChannel out = new FileOutputStream(outputFile).getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(list.get(i).getLength());
            in.read(buffer, list.get(i).getSkipSize());
            buffer.flip();
            out.write(buffer);
            in.close();
            out.close();
            result.add(outputFile);
        }
        return result;
    }

    /**
     * 对文件进行切分 1.先根据指定的参数分片,每个分片以\n结束 2。根据分片的情况,计算切点
     */
    private List<Point> blocking(File file, int piece) throws IOException {
        List<Point> result = new ArrayList<Point>();
        List<Long> list = new ArrayList<Long>();
        list.add(-1L);
        long length = file.length();
        long step = length / piece;
        long index = 0;
        for (int i = 0; i < piece; i++) {
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
            if (index + step < length) {
                index = index + step;
                in.skip(index);

                while (in.read() != 10) {
                    index = index + 1;
                }
                list.add(index);
                index++;
            }
            in.close();
        }
        list.add(length - 1);
        System.out.println(list);
        for (int i = 0; i < list.size() - 1; i++) {
            long skipSize = list.get(i) + 1;
            long l = list.get(i + 1) - list.get(i);
            result.add(new Point(skipSize, l));

        }
        System.out.println(result);
        return result;
    }

    /**
     * 切分文件的切点 skipSize指的是从流跳过的size length指的是从流读出的长度
     */
    private class Point {
        public Point(long skipSize, long length) {
            if (length > Integer.MAX_VALUE) {
                throw new RuntimeException("长度溢出");
            }
            this.skipSize = skipSize;
            this.length = (int) length;
        }

        @Override
        public String toString() {
            return "Point [skipSize=" + skipSize + ", length=" + length + "]\n";
        }

        private long skipSize;
        private int length;

        public long getSkipSize() {
            return skipSize;
        }

        public int getLength() {
            return length;
        }

    }
}
