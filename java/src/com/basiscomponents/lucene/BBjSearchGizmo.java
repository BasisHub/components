package com.basiscomponents.lucene;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.facet.FacetResult;
import org.apache.lucene.facet.Facets;
import org.apache.lucene.facet.FacetsCollector;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.taxonomy.FastTaxonomyFacetCounts;
import org.apache.lucene.facet.taxonomy.TaxonomyReader;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyReader;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyWriter;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;



public class BBjSearchGizmo
{
    private String directoryName;
    private String taxoDirectoryName;

    private Analyzer analyzer;
    private IndexWriterConfig iwc;



    public BBjSearchGizmo(String directory) throws IOException
    {
        this.directoryName = directory;
        analyzer = new WhitespaceAnalyzer();
        this.iwc=new IndexWriterConfig(analyzer);
    }



    public void addDocument(BBjSearchGizmoDoc doc) throws IOException, LockObtainFailedException
    {
        FSDirectory indexdirectory = FSDirectory.open(FileSystems.getDefault().getPath(directoryName));
        IndexWriter writer = new IndexWriter(indexdirectory,iwc);

        //remove it first
        try
        {
            MultiFieldQueryParser queryparser = new MultiFieldQueryParser(new String[]{"id"},analyzer);
            Query query = queryparser.parse(doc.getId());
            writer.deleteDocuments(query);
        }
        catch (ParseException ex) { }
        catch (IOException ex) {}

        /*
        Document lucene_doc_taxo2= new Document();
        lucene_doc_taxo2.add(new FacetField("id","2"));
        lucene_doc_taxo2.add(new FacetField("color","red"));
        writer.addDocument(config.build(taxoWriter, lucene_doc_taxo2));
        */

        Document lucene_doc = new Document();
        lucene_doc.add(new StringField("id",doc.getId(),Field.Store.YES));

        Iterator<BBjSearchGizmoDocField>it = doc.getFields().iterator();
        while (it.hasNext())
        {
            BBjSearchGizmoDocField f = it.next();
            TextField lucene_field = new TextField(f.getName(),f.getContent().toLowerCase(),Field.Store.YES);
            lucene_field.setBoost(f.getBoost());
            lucene_doc.add(lucene_field);

            if (f.isFacet()){
                //paths.add(new CategoryPath(f.getName(),f.getContent()));
                //FacetField lucene_field_taxo=new FacetField(f.getName(), paths);
                System.out.println(f.getName());
                System.out.println(f.getContent());
            }

            System.out.println(lucene_field.toString());
        }

        //System.out.println(paths);
        try
        {
            writer.addDocument(lucene_doc);
        }
        catch (Exception ex) {
            writer.close();
            //taxoWriter.commit();

            indexdirectory.close();

            throw ex;
        }

        writer.close();
        //taxoWriter.commit();

        indexdirectory.close();
    }


    public void removeDocument(String id) throws IOException, LockObtainFailedException, ParseException
    {
        FSDirectory indexdirectory = FSDirectory.open(FileSystems.getDefault().getPath(directoryName));
        IndexWriter writer = new IndexWriter(indexdirectory,iwc);
        MultiFieldQueryParser queryparser = new MultiFieldQueryParser(new String[]{id}, analyzer);
        Query query = queryparser.parse(id);
        writer.deleteDocuments(query);
        writer.close();
        indexdirectory.close();
    }


    public ArrayList<String> doSearch(String searchTerm, String[] fields) throws IOException, ParseException
    {
        FSDirectory indexdirectory = FSDirectory.open(FileSystems.getDefault().getPath(directoryName));
        IndexReader reader = DirectoryReader.open(indexdirectory);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopScoreDocCollector collector = TopScoreDocCollector.create(reader.numDocs());


        //note: this is how to get to the atomic reader field in case we need it...
        //System.out.println(dr.leaves().get(0).reader().getFieldInfos().fieldInfo(0).name);
        //System.out.println(dr.leaves().get(0).reader().getFieldInfos().fieldInfo(1).name);
        //System.out.println(dr.leaves().get(0).reader().getFieldInfos().fieldInfo(2).name);


        MultiFieldQueryParser queryparser = new MultiFieldQueryParser(fields, analyzer);
        queryparser.setAllowLeadingWildcard(true);
        Query query = queryparser.parse(searchTerm.toLowerCase());
        searcher.search(query,collector);
        ScoreDoc[] hits = collector.topDocs().scoreDocs;

        ArrayList<String> result = new ArrayList<String>();

        for (int i=0; i<hits.length; ++i)
        {
            int docId = hits[i].doc;
            Document d = searcher.doc(docId);
            result.add(d.get("id"));
        }
        reader.close();
        indexdirectory.close();

        return result;
    }


    public ArrayList<String> doFuzzySearch(String searchTerm, String field) throws IOException
    {
        FSDirectory indexdirectory = FSDirectory.open(FileSystems.getDefault().getPath(directoryName));
        IndexReader reader = DirectoryReader.open(indexdirectory);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopScoreDocCollector collector = TopScoreDocCollector.create(1000);

        searchTerm=searchTerm+"~";
        Term fuzzyclientsearch = new Term(field,searchTerm);
        FuzzyQuery fuzzyquery = new FuzzyQuery(fuzzyclientsearch);
        searcher.search(fuzzyquery,collector);
        ScoreDoc[] hits = collector.topDocs().scoreDocs;

        ArrayList<String> result = new ArrayList<String>();

        for (int i=0; i<hits.length;++i)
        {
            int docId = hits[i].doc;
            Document d = searcher.doc(docId);
            result.add(d.get("id"));
        }
        indexdirectory.close();
        return result;
    }


    public List<FacetResult> facetsWithSearch(String searchTerm, String[] fields) throws IOException, ParseException
    {
        FSDirectory indexdirectory = FSDirectory.open(FileSystems.getDefault().getPath(directoryName));
        FSDirectory indextaxodirectory = FSDirectory.open(FileSystems.getDefault().getPath(taxoDirectoryName));
        IndexReader reader = DirectoryReader.open(indexdirectory);
        TaxonomyReader taxoreader = new DirectoryTaxonomyReader(indextaxodirectory);
        IndexSearcher searcher = new IndexSearcher(reader);
        MultiFieldQueryParser queryparser = new MultiFieldQueryParser(fields, analyzer);
        queryparser.setAllowLeadingWildcard(true);
        Query query = queryparser.parse(searchTerm.toLowerCase());

        FacetsCollector fc = new FacetsCollector();
        FacetsCollector.search(searcher, query, 10, fc);

        List<FacetResult> results = new ArrayList<FacetResult>();

        FacetsConfig config = new FacetsConfig();
        Facets facets = new FastTaxonomyFacetCounts(taxoreader,config,fc);
        results.add(facets.getTopChildren(3, "color"));

        reader.close();
        taxoreader.close();
        indexdirectory.close();
        indextaxodirectory.close();

        return results;
    }

}
