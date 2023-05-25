import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class PositionalIndex
{
    ArrayList<String> termList;
    ArrayList<ArrayList<DocId>> docLists;
    int distance = 1;
    private static Set<String> links= new HashSet<>();


    public PositionalIndex(ArrayList<String> docs) {
        termList = new ArrayList<>();
        docLists = new ArrayList<>();

        for (int i = 0; i < (docs.size()); i++) {
            String all_lines = docs.get(i);
            String[] tokens = all_lines.split("[ .,?!:;$%&*+()%#!/\\-\\^\"]+");

            for (int m = 0; m < tokens.length; m++) {
                tokens[m] = tokens[m].toLowerCase();
            }
            for (int j = 0; j < (tokens.length); j++)
            {
                if (tokens[j].equals("a") || tokens[j].equals("an") || tokens[j].equals("the")
                        || tokens[j].equals("and") || tokens[j].equals("or") || tokens[j].equals("not")
                        || tokens[j].equals("to") || tokens[j].equals("at"))
                    continue;
                if (!termList.contains(tokens[j]))
                {
                    termList.add(tokens[j]);
                    DocId doid = new DocId(i, j);
                    ArrayList<DocId> ListOfDoc = new ArrayList<DocId>();
                    ListOfDoc.add(doid);
                    docLists.add(ListOfDoc);
                } else {
                    int index = termList.indexOf(tokens[j]);
                    ArrayList<DocId> docList = docLists.get(index);
                    boolean match = false;
                    int k = 0;
                    // old term same document also seen before
                    for (DocId doid : docList) {
                        if (doid.docId == i) {
                            doid.insertPosition(j);
                            match = true;
                        }
                        k++;
                    }
                    // old term new document
                    if (!match) {
                        DocId doid = new DocId(i, j);
                        docLists.get(index).add(doid);
                    }
                }
            }
        }
    }

    public String toString() {
        String matrixString = new String();
        ArrayList<DocId> docList;
        for (int i = 0; i < termList.size(); i++) {
            matrixString += String.format("%-15s", termList.get(i));
            docList = docLists.get(i);
            for (int j = 0; j < docList.size(); j++) {
                matrixString += docList.get(j) + "\t";
            }
            matrixString += "\n";
        }
        return matrixString;
    }

    public ArrayList<DocId> intersect(ArrayList<DocId> list1, ArrayList<DocId> list2) {
        if (list1 == null)
            return list2;
        else if (list2 == null)
            return list1;
        ArrayList<DocId> mergedList = new ArrayList<DocId>();
        int index_list1 = 0, index_list2 = 0;

        while (index_list1 < list1.size() && index_list2 < list2.size())
        {
            if (list1.get(index_list1).docId == list2.get(index_list2).docId)
            {

                ArrayList<Integer> pp1 = list1.get(index_list1).positionList;
                ArrayList<Integer> pp2 = list2.get(index_list2).positionList;
                int pid1 = 0, pid2 = 0;
                boolean match = false;
                while (pid1 < pp1.size()) {
                    pid2 = 0;
                    while (pid2 < pp2.size()) {
                        if (pp2.get(pid2) - pp1.get(pid1) == distance) {
                            if (!match)
                            {
                                DocId docList = new DocId(list1.get(index_list1).docId);
                                mergedList.add(docList);
                                match = true;
                            }

                        }
                        pid2++;
                    }
                    pid1++;
                }
                index_list1++;
                index_list2++;
            } else if (list1.get(index_list1).docId > list2.get(index_list2).docId)
                index_list2++;
            else
                index_list1++;
        }
        return mergedList;
    }

    //
    public ArrayList<DocId> phraseQuery(String[] query) {
        distance = 1;
        ArrayList<DocId> docList1 ;
        ArrayList<DocId> docList2 ;
        ArrayList<DocId> docList ;

        if (query.length == 0)
            return null;
        else if (query.length == 1) {
            if (termList.contains(query[0])) {
                int index = termList.indexOf(query[0]);
                return docLists.get(index);
            } else
            {
                return null; // Word not found
            }
        }
        else
        {
            ArrayList<DocId> result = new ArrayList<DocId>();
            if (termList.contains(query[0]))
                docList1 = docLists.get(termList.indexOf(query[0]));
            else
                docList1 = null;
            if (termList.contains(query[0]))
                docList2 = docLists.get(termList.indexOf(query[1]));
            else
                docList2 = null;
            result = intersect(docList1, docList2);
            distance++;
            for (int i = 2; i < query.length; i++) {
                if (termList.contains(query[i]))
                    docList = docLists.get(termList.indexOf(query[i]));
                else
                    docList = null;
                result = intersect(result, docList);
                distance++;
            }
            return result;
        }
    }

    public static double calculateCossallarlty(String Doc, String Doc2) {
        int firstDoccounter = 0, secondDoccounter2 = 0;
        int numerator = 0;
        int sum_numerator = 0;
        double sqr_sumDoc1 = 0, sqr_sumDoc2 = 0;
        double cosineSimilarlty;

        ArrayList<String> Doc1_words = new ArrayList<>(); // files System.out.println(result);
        ArrayList<String> Doc2_words = new ArrayList<>();// query

        // Split the sentence into words
        String[] words = Doc.split(" ");
        for (String word : words) {
            Doc1_words.add(word);
        }
        HashSet<String> allwords = new HashSet<>(Doc1_words);
        String[] words2 = Doc2.split(" ");
        for (String word : words2) {
            Doc2_words.add(word);
            allwords.add(word);
        }

        for (String word : allwords)
        {
            for (String w : Doc1_words)
            {
                if (word.equals(w)) {
                    firstDoccounter++;
                }
            }
            for (String w : Doc2_words)
            {
                if (word.equals(w)) {
                    secondDoccounter2++;
                }
            }
            sqr_sumDoc1 += Math.pow(firstDoccounter, 2.0);
            sqr_sumDoc2 += Math.pow(secondDoccounter2, 2.0);// نفس الشيء
            numerator = firstDoccounter * secondDoccounter2;// ؟
            sum_numerator += numerator;//
            firstDoccounter = secondDoccounter2 = 0;
        }

        sqr_sumDoc1 = Math.sqrt(sqr_sumDoc1);// بيجيب الجذر

        sqr_sumDoc2 = Math.sqrt(sqr_sumDoc2);// نفس شيء
        cosineSimilarlty = sum_numerator / (sqr_sumDoc1 * sqr_sumDoc2);//
        return cosineSimilarlty;
    }

    public Map<String, Map<Integer, Double>> calculateTFIDF() {
        Map<String, Map<Integer, Double>> tfidfMap = new HashMap<>();

        int totalDocuments = docLists.size();
        for (int i = 0; i < termList.size(); i++) {
            String term = termList.get(i);
            ArrayList<DocId> docList = docLists.get(i);
            int documentFrequency = docList.size();

            Map<Integer, Double> tfidfValues = new HashMap<>();
            for (DocId doc : docList) {
                int docId = doc.docId;
                int termFrequency = doc.positionList.size();
                double tf = (double) termFrequency / docLists.get(docId).size();
                double idf = Math.log((double) totalDocuments / documentFrequency);
                double tfidf = tf * idf;
                tfidfValues.put(docId, tfidf);
            }

            tfidfMap.put(term, tfidfValues);
        }

        return tfidfMap;
    }

    private static String readFile(String filename) throws FileNotFoundException {
        Scanner sc = new Scanner(new File(filename));
        StringBuilder sb = new StringBuilder();
        while (sc.hasNextLine()) {
            sb.append(sc.nextLine());
        }
        sc.close();
        return sb.toString();
    }

    public static void main(String[] args) throws FileNotFoundException {
        String[] filenames = { "file1.txt", "file2.txt", "file3.txt", "file4.txt", "file5.txt", "file6.txt",
                "file7.txt", "file8.txt", "file9.txt", "file10.txt" };
        ArrayList<String> docs = new ArrayList<>();
        HashMap<Double, Integer> Result_Cos = new HashMap<Double, Integer>();
        for (int i = 0; i < 10; i++) {
            String content_file = readFile(filenames[i]);
            docs.add(content_file);
        }
        Scanner scanner = new Scanner(System.in);
        PositionalIndex pi = new PositionalIndex(docs);
        System.out.println("\t"+"\t"+"\t"+"\t"+"Requirement 1");
        System.out.println("\t"+"\t"+"\t"+"\t"+"_____________"+'\n');

        System.out.print(pi);
        System.out.println('\n'+"\t"+"\t"+"\t"+"\t"+"Requirement 2");
        System.out.println("\t"+"\t"+"\t"+"\t"+"_____________"+'\n');

        System.out.println("Enter a phrase query ");
        String phraseQuery = scanner.nextLine().toLowerCase();
        String[] tokens = phraseQuery.split("[ .,?!:;$%&*+()%#!/\\-\\^\"]+");
        ArrayList<DocId> result = pi.phraseQuery(tokens);
        if (result == null) {
            System.out.println('\n'+"Not found");
        } else {
            System.out.print('\n'+"this documents  ");
            for (DocId res : result) {
                System.out.print(res.docId + 1 + " ");
            }
            System.out.println("Satytisy the query"+'\n');

            System.out.println("\t"+"\t"+"\t"+"\t"+"Requirement 3");
            System.out.println("\t"+"\t"+"\t"+"\t"+"_____________"+'\n');

            double result_cos;
            TreeMap<Double, ArrayList<Integer>> map = new TreeMap<>();

            {
                for (int i = 0; i < 10; i++) {
                    result_cos = calculateCossallarlty(docs.get(i), phraseQuery);
                    if (map.containsKey(result_cos))
                        map.get(result_cos).add(i + 1);
                    else {
                        ArrayList<Integer> documents = new ArrayList<>();
                        documents.add(i + 1);
                        map.put(result_cos, documents);
                    }

                }
            }
            System.out.println('\n'+"Rank files by the value of the cosin similarity:");
            for (double key : map.descendingKeySet()) {
                ArrayList<Integer> value = map.get(key);

                if (key != 0)
                    System.out.println("File" + value + " With value: " + key);
            }
        }


        Map<String, Map<Integer, Double>> tfidfMap = pi.calculateTFIDF();

        // printing TF-IDF values for a specific term
        System.out.println('\n'+"Enter a Term ");
        String term = scanner.nextLine().toLowerCase();
        Map<Integer, Double> termTFIDF = tfidfMap.get(term);
        if (termTFIDF != null) {
            for (Map.Entry<Integer, Double> entry : termTFIDF.entrySet()) {
                int docId = entry.getKey();
                double tfidf = entry.getValue();
                System.out.println("Term: " + term + ", Document: " + (docId + 1) + ", TF-IDF: " + tfidf);
            }
        } else {
            System.out.println("Term not found.");
        }

        String url = "https://www.wikipedia.org/";
        getPageLinks(url);

    }
    public static void getPageLinks(String URL) {
        //4. Check if you have already crawled the URLs
        //(we are intentionally not checking for duplicate content in this example)
        if (!links.contains(URL)) {
            try {
                //4. (i) If not add it to the index
                if (links.add(URL)) {
                    System.out.println(URL);
                }

                //2. Fetch the HTML code
                Document document = Jsoup.connect(URL).get();//jsoup jar to extract web data
                //3. Parse the HTML to extract links to other URLs
                Elements linksOnPage = document.select("a[href]");

                //5. For each extracted URL... go back to Step 4.
                for (Element page : linksOnPage) {
                    getPageLinks(page.attr("abs:href"));
                }
            } catch (IOException e) {
                System.err.println("For '" + URL + "': " + e.getMessage());
            }
        }
    }

}
