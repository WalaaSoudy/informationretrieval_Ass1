//walaa soudy ibrahim ibrahim        20201218
//yara mohamed saad                  20200629
//marwa mahmoud ismail               20201160
import java.io.*;
import java.util.*;

public class InvertedIndex {
    public static void main(String[] args) throws IOException {
        // Step 1: Read 10 text files
        String[] fileNames = {"files/file1.txt", "files/file2.txt", 
            "files/file3.txt", "files/file4.txt", "files/file5.txt",
            "files/file6.txt", "files/file7.txt", "files/file8.txt", 
            "files/file9.txt", "files/file10.txt"};

        // Step 2: Build the inverted index
        HashMap<String, DictEntry> index = buildIndex(fileNames);

        // Step 3: Read a word and list all files containing the word
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter a word: ");
        String query = scanner.next();
        List<Integer> docIds = searchIndex(index, query);
        if (docIds.isEmpty()) {
            System.out.println("The word \"" + query + "\" does not appear in any of the files.");
        } else {
            System.out.println("The word \"" + query + "\" appears in the following files:");
            for (int docId : docIds) {
                System.out.println("  " + fileNames[docId]);
            }
        }
    }

    // Builds the inverted index for the given list of file names
    public static HashMap<String, DictEntry> buildIndex(String[] fileNames) throws IOException {
        HashMap<String, DictEntry> index = new HashMap<>();
        int docId = 0;
        for (String fileName : fileNames) {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] terms = line.split("\\s+");
                for (String term : terms) {
                    term = term.toLowerCase();
                    DictEntry entry = index.get(term);
                    if (entry == null) {
                        entry = new DictEntry();
                        index.put(term, entry);
                    }
                    entry.term_freq++;
                    Posting posting = entry.pList;
                    if (posting == null || posting.docId != docId) {
                        posting = new Posting();
                        posting.docId = docId;
                        posting.next = entry.pList;
                        entry.pList = posting;
                        entry.doc_freq++;
                    } else {
                        posting.dtf++;
                    }
                }
            }
            reader.close();
            docId++;
        }
        return index;
    }

    // Searches the inverted index for the given query term
    public static List<Integer> searchIndex(HashMap<String, DictEntry> index, String query) {
        List<Integer> docIds = new ArrayList<>();
        DictEntry entry = index.get(query.toLowerCase());
        if (entry != null) {
            Posting posting = entry.pList;
            while (posting != null) {
                docIds.add(posting.docId);
                posting = posting.next;
            }
        }
        return docIds;
    }
}

// Linked list node for the posting list
class Posting {
    public Posting next = null;
    public int docId;
    public int dtf = 1;
}

// Entry in the index dictionary
class DictEntry {
    public int doc_freq = 0;
    public int term_freq = 0;
    public Posting pList = null;
}