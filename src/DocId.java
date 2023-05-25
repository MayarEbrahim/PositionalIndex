import java.util.ArrayList;

public class DocId {
    int docId;
    ArrayList<Integer> positionList;
    public DocId(int did)
    {
        docId = did;
        positionList = new ArrayList<Integer>();
    }
    public DocId(int did, int position)
    {
        docId = did;
        positionList = new ArrayList<Integer>();
        positionList.add(position);
    }

    public void insertPosition(int position)
    {
        positionList.add(position);
    }

    public String toString()
    {
        String docIdString = ""+docId + ":[";
        for(Integer pos:positionList)
            docIdString += pos + ",";
        docIdString = docIdString.substring(0,docIdString.length()-1) + "]";
        return docIdString;
    }
}
