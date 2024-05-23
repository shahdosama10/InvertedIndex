package crawler;

/**
 *
 * @author ehab
 */
import invertedIndex.SourceRecord;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
//==============================================================================

public class WebCrawlerWithDepth {

    private static final int MAX_DEPTH =2;
    private static final int MAX_PER_PAGE = 6;
    int max_docs = 20;
    private HashSet<String> links;
    Map<Integer, SourceRecord> sources;
    //   Map<Integer, String> doc_text;
    int fid = 0;
    int plinks = 0;
    //String storageName;
//==============================================================================

    public WebCrawlerWithDepth() {
        links = new HashSet<>();
        sources = null;
        fid = 0;
    }

    public WebCrawlerWithDepth(invertedIndex.Index5 in) {
        links = new HashSet<>();
        sources = in.sources;
        fid = 0;
    }

    public void setSources(invertedIndex.Index5 in) {
        sources = in.sources;
    }
//==============================================================================

    public String getText(Document document) {
        String pAcc = "";
        Elements p = document.body().getElementsByTag("p");

        for (Element e : p) {
            pAcc += e.text();
        }
        return pAcc;
    }
//==============================================================================

    public void getPageLinks(String URL, int depth, invertedIndex.Index5 index) {
       System.out.println("|| URL: [" + URL + "] --------  depth: " + depth + " fid=" + fid + " plinks=" + plinks + "\t|||| ");

        if ((!(links.contains(URL)))
                && (depth < MAX_DEPTH)
                && (fid < max_docs)
                //        && ((depth == 1) || (plinks < (MAX_PER_PAGE * (depth + 1))))
                && ((depth == 0)
                || ((depth == 1) && (plinks < ((MAX_PER_PAGE) + 290)))
                || (plinks < ((MAX_PER_PAGE * (depth + 1)) - (plinks / 2))))
                && (!URL.contains("https://.m."))
                && (URL.contains("https://en.w"))
                && (!URL.contains("wiki/Wikipedia"))
                && (!URL.contains("searchInput"))
                && (!URL.contains("wiktionary"))
                && (!URL.contains("#"))
                && (!URL.contains(","))
                && (!URL.contains("Wikiquote"))
                && (!URL.contains("disambiguation"))
                && (!URL.contains("w/index.php"))
                && (!URL.contains("wikimedia"))
                && (!URL.contains("/Privacy_policy"))
                && (!URL.contains("Geographic_coordinate_system"))
                && (!URL.contains(".org/licenses/"))
                && ((!URL.substring(12).contains(":")) || (depth == 0)) // ignor sublink that contain : bu pass the "http:"
                && (!URL.isEmpty())
                && (!URL.contains("Main_Page"))
                && (!URL.contains("mw-head"))) {
            try {
                //??????------------------------------------------------------------------------                
                // *** 1-  add this URL to the  visited list
                links.add(URL);
                // inititalize the document element using the Jsoup library

                Document document = Jsoup.connect(URL).timeout(10000).get();

                
                //??????------------------------------------------------------------------------                
                // *** 2-  get all links of the page         
                // use document select  with parameter "a[href]")

                Elements linksOnPage = document.select("a[href]");

                
                
                //??????------------------------------------------------------------------------
                // *** 3-  get all  paragtaphs <p></p>  elments from the page (document)

                Elements p = document.body().getElementsByTag("p");

                
                //??????------------------------------------------------------------------------
                //**** 4-  get the text inside those parargraphs inside the tags <p></p>
                //***       accumulate then into  to String docText
               String docText = "";

               for(Element element : p){
                   docText += element.text()+ " ";
               }


              //****     build the sourses (given)
               SourceRecord sr = new SourceRecord(fid, URL, document.title(), docText.substring(0, 30));
                sr.length = docText.length();
                sources.put(fid, sr);
                
                //??????------------------------------------------------------------------------
                //**** 5-  pass the docText for the inverted index with the doc id

                index.buildIndex(docText ,fid);
                
                
                plinks++;  // accumulator for the link in a sub-branch
                fid++;   // current document id
               

                for (Element page : linksOnPage) {
                //**** 6-  handle all the page hyper links "linksOnPage" you obtained from step 2 recursivly with depth +1
                //             Hint ::    Use  page.attr("abs:href") for each page

                    String nextPageUrl = page.attr("abs:href");
                    parsePageLinks(nextPageUrl , depth +1 , index);
                }
                 plinks--;
            } catch (IOException e) {
                System.err.println("For '" + URL + "': " + e.getMessage());
            }
        }
    }
//==============================================================================

    public void parsePageLinks(String URL, int depth, invertedIndex.Index5 index) {
        System.out.println("--------------- URL: " + URL + " --------  depth: " + depth + " - - - - - - --------- ");

        plinks = 0;
        getPageLinks(URL, depth, index);
    }
//==============================================================================

    public String getSourceName(int id) {
        return sources.get(id).getURL();
    }
//==============================================================================

    void printSources() {
        for (int i = 0; i < sources.size(); i++) {
            System.out.println(">>  " + i + " [" + getSourceName(i) + "]");
        }
    }

    public invertedIndex.Index5 initializeNew(String storageName) {
        invertedIndex.Index5 index = new invertedIndex.Index5();
        setSources(index);
        index.createStore(storageName);
        return index;
    }

    public invertedIndex.Index5 initialize(String storageName) {
        invertedIndex.Index5 index = new invertedIndex.Index5();
        setSources(index);
        setDomainKnowledge(index, storageName);
        index.setN(fid);
        index.store(storageName);

        return index;
    }

    void setDomainKnowledge(invertedIndex.Index5 index, String domain) {
        if (domain.equals("test")) {
            parsePageLinks("https://en.wikipedia.org/wiki/List_of_pharaohs", 0, index);
            parsePageLinks("https://en.wikipedia.org/wiki/Cairo", 0, index);

        }

    }

//==============================================================================
    public static void main(String[] args) {

        WebCrawlerWithDepth wc = new WebCrawlerWithDepth();

        invertedIndex.Index5 index = wc.initialize("test"); //   ukraine
            index.find_07a("narmer giza pyramid");
            index.searchLoop();
    }
}
