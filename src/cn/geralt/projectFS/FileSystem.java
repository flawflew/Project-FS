package cn.geralt.projectFS;

import cn.geralt.util.ByteIO;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FileSystem {
    private String VHDDir;
    private SuperBlock superBlock;
    private boolean isInitialized;
    private DEntry root;
    private DEntry current;
    private Map<Integer,MyFile> files = new HashMap<>();
    private byte[] iNodeMap;
    private byte[] blockMap;

    public String getVHDDir() {
        return VHDDir;
    }

    public SuperBlock getSuperBlock() {
        return superBlock;
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public Map<Integer, MyFile> getFiles() {
        return files;
    }

    public FileSystem(String VHDDir) throws IOException {
        this.VHDDir = VHDDir;
        current = null;
        superBlock = new SuperBlock(this);
        boolean checked = diskCheck(this.VHDDir);
        if(checked){
            this.isInitialized = initialize();
        }
        else{
            format();
            this.isInitialized = initialize();
        }
    }
    private boolean diskCheck(String dir){
        //TODO: disk check

        return false;
    }
    private boolean initialize() throws IOException {
        //TODO: 1.initialize the super block

        root = new DEntry(this,null,superBlock.getRootINode(),getINode(superBlock.getRootINode()));
        iNodeMap = superBlock.getINodeMap();
        blockMap = superBlock.getBlockMap();
        System.out.println("root:"+root.getFileName());
        return true;
    }

    public int getUnusedNum(byte[] bytes){
        int num = 0;
        for (byte aByte : bytes) {
            for (int i = 0; i < 8 ; i++) {
                if((aByte&(byte)0b10000000) == 0) return num;
                num++;
                aByte = (byte)(aByte<<1);
            }
        }
        return -1;
    }

    public void setMapBit(byte[] bytes,int pos,boolean bit){
        int a = pos/8;
        int b = pos%8;
        byte temp = bytes[a];
        byte mask = (byte)(0b10000000 >> b);
        if((bit&&((byte)(temp&mask)!=0))||(!bit&&((byte)(temp&mask)==0))){

        }
        else {
            temp = (byte) (temp ^ mask);
            bytes[a] = temp;
        }
    }
//
//    public boolean isDirExist(String relDir, DEntry cur){
//        String[] path = relDir.split("/");
//        for (String s : path) {
//            if(cur.isChildExist(s)){
//                cur = cur.getChild(s);
//            }
//            else{
//                return false;
//            }
//        }
//
//        return true;
//    }

    public void createFile(String absDir){
        //TODO

    }

    private int getFD(){
        for (int i = 0; i < files.size()+1; i++) {
            if(!files.containsKey(i)){
                return i;
            }
        }
        return -1;
    }

    public int open(String dir,DEntry cur){
        DEntry temp = DEntry.getInstance(dir,cur);
        if(temp==null){
            return -1;
        }
        else{
            int fd = getFD();
            MyFile myFile = MyFile.getInstance(temp,fd);
            files.put(fd,myFile);
            return fd;
        }
    }

    private int close(int fd){
        files.remove(fd);
        return 1;
    }

    public int read(int fd,byte[] des,int off,int len) throws IOException {
        MyFile file = files.get(fd);
        byte[] data = file.read();
        System.arraycopy(data,off,des,0,len);
        return len;
    }

    public void write(){

    }

    private void format() throws IOException {
        int[] ints = {31415926,4096,16*1024*1024,256,4096,4096,4096,1052672,3837,0,48,560};
        superBlock.format(ints);
        System.out.println("formatted!");
    }

    public INode getINode(int iNodeNum) throws IOException {
        return new INode(superBlock,iNodeNum);
    }

    public static byte[] getbytes(int offset, int len) throws IOException {
        ByteIO byteIO = new ByteIO("src/cn/geralt/util/mydisk.vhd");
        return byteIO.output(offset,len);
    }

    public void newDir(){

    }

    public static void main(String[] args) throws IOException {
        FileSystem fileSystem = new FileSystem("src/cn/geralt/util/mydisk.vhd");
        System.out.println();
    }
}
