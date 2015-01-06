package com.basiscomponents.lucene;



import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import org.apache.lucene.analysis.Analyzer;
//import org.apache.lucene.analysis.compound.hyphenation.TernaryTree.Iterator;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.facet.DrillDownQuery;
import org.apache.lucene.facet.DrillSideways;
import org.apache.lucene.facet.DrillSideways.DrillSidewaysResult;
import org.apache.lucene.facet.FacetField;
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
import org.apache.lucene.util.Version;


public class BBjSearchGizmo 
{
	private String directoryName;

	//-----------atributes of the index
	public FSDirectory indexdirectory;
	public FSDirectory indextaxodirectory;
	private Analyzer analyzer;
	//-----------
	//----------- atributes necesary for the search method
	protected IndexSearcher indexsearcher;
	protected IndexReader reader;
	protected TaxonomyReader taxoreader;
	protected IndexWriterConfig iwc;
	protected IndexWriter writer;
	protected IndexSearcher searcher;
	protected TopScoreDocCollector collector;
	protected int numberOfHits;
	protected boolean docsInOrder;
	protected MultiFieldQueryParser queryparser;
	
	protected Query query;
	protected ScoreDoc [] hits;
	protected FuzzyQuery fuzzyquery;
	protected Document doc;
	//protected TokenStream token;
	//protected LowerCaseTokenizer lowercasetokenizer;
	//protected InputStreamReader inputstreamreader;	
	protected String taxoDirectoryName;
	protected DirectoryTaxonomyWriter taxoWriter;
	protected FacetsConfig config;
	
	
	
	
	public BBjSearchGizmo(String directory) throws IOException
	{
		this.directoryName = directory;
		analyzer = new WhitespaceAnalyzer(Version.LUCENE_48);
	}
	

	
	public void addDocument(BBjSearchGizmoDoc doc) throws IOException, ParseException
	{
		//remove it first
		//this should work like a WRITE in BB and overwrite an existing doc
		this.removeDocument(doc.getId());
		//List<CategoryPath> paths= new ArrayList<CategoryPath>();
		
		indexdirectory=FSDirectory.open(new File(directoryName));	
		this.iwc=new IndexWriterConfig(Version.LUCENE_48,analyzer);
		writer= new IndexWriter(indexdirectory,iwc);
		
		
		
		//lucene_doc_taxo.add(new FacetField("color","green"));
		
		
		/*
		Document lucene_doc_taxo2= new Document();
		lucene_doc_taxo2.add(new FacetField("id","2"));
		lucene_doc_taxo2.add(new FacetField("color","red"));
		writer.addDocument(config.build(taxoWriter, lucene_doc_taxo2));
		*/
		
		
		
		
		
		
		
		Document lucene_doc=new Document ();
		lucene_doc.add(new TextField("id",doc.getId(),Field.Store.YES));
		
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
			writer.addDocument(lucene_doc);	
			//writer.addDocument(config.build(lucene_doc_taxo));
			
			
			writer.close();
			//taxoWriter.commit();
			
			
			indexdirectory.close();
			
		}
		
	
		

	

	public void removeDocument(String id) throws IOException, ParseException
	{
		indexdirectory=FSDirectory.open(new File(directoryName));	
		this.iwc=new IndexWriterConfig(Version.LUCENE_48,analyzer);
		writer= new IndexWriter(indexdirectory,iwc);
		String [] fields={"id"};
		// here we can apply the same solution as in do search or just define that a user can only remove by the id of the document
		MultiFieldQueryParser queryparser=new MultiFieldQueryParser(Version.LUCENE_48,fields, analyzer);
		Query query=queryparser.parse(id);
		writer.deleteDocuments(query);
		writer.close();
	}
	
	public ArrayList<String> doSearch(String searchTerm, String [] fields) throws IOException, ParseException
	{

		indexdirectory=FSDirectory.open(new File(directoryName));
		IndexReader reader=DirectoryReader.open(indexdirectory);
		IndexSearcher searcher = new IndexSearcher(reader);		
		TopScoreDocCollector collector= TopScoreDocCollector.create(1000, true);
		
		DirectoryReader dr = DirectoryReader.open(indexdirectory);
		
//		note: this is how to get to the atomic reader field in case we need it...		
//		System.out.println(dr.leaves().get(0).reader().getFieldInfos().fieldInfo(0).name);
//		System.out.println(dr.leaves().get(0).reader().getFieldInfos().fieldInfo(1).name);
//		System.out.println(dr.leaves().get(0).reader().getFieldInfos().fieldInfo(2).name);		

		

			
		MultiFieldQueryParser queryparser=new MultiFieldQueryParser(Version.LUCENE_48,fields, analyzer);
		queryparser.setAllowLeadingWildcard(true);
		Query query=queryparser.parse(searchTerm.toLowerCase());
		searcher.search(query,collector);
		ScoreDoc [] hits= collector.topDocs().scoreDocs;		
		
		ArrayList<String> result = new ArrayList<>();

				{
					for (int i=0; i<hits.length;++i)
					{
						int docId= hits[i].doc;
						Document d= searcher.doc(docId);
						result.add(d.get("id"));
//						System.out.println(hits[i]);
//						System.out.println("Found "+hits.length +" hits" );
//						System.out.println(+"  score="+hits[i].score);
					}
				}
		indexdirectory.close();
		return result;
		
	}

	public ArrayList <String> doFuzzySearch(String searchTerm, String field) throws IOException{
		indexdirectory=FSDirectory.open(new File(directoryName));
		IndexReader reader=DirectoryReader.open(indexdirectory);
		IndexSearcher searcher = new IndexSearcher(reader);		
		TopScoreDocCollector collector= TopScoreDocCollector.create(1000, true);

		searchTerm=searchTerm+"~";
		Term fuzzyclientsearch=new Term(field,searchTerm);
		FuzzyQuery fuzzyquery= new FuzzyQuery(fuzzyclientsearch);
		searcher.search(fuzzyquery,collector);
		ScoreDoc [] hits= collector.topDocs().scoreDocs;		
		
		ArrayList<String> result = new ArrayList<>();

				{
					for (int i=0; i<hits.length;++i)
					{
						int docId= hits[i].doc;
						Document d= searcher.doc(docId);
						result.add(d.get("id"));
//						System.out.println(hits[i]);
//						System.out.println("Found "+hits.length +" hits" );
//						System.out.println(+"  score="+hits[i].score);
					}
				}
		indexdirectory.close();
		return result;
		
		
	}
	
	public List<FacetResult> facetsWithSearch(String searchTerm, String [] fields) throws IOException, ParseException{
		indexdirectory=FSDirectory.open(new File(directoryName));
		indextaxodirectory=FSDirectory.open(new File(taxoDirectoryName));
		reader=DirectoryReader.open(indexdirectory);
		taxoreader= new DirectoryTaxonomyReader(indextaxodirectory);
		searcher = new IndexSearcher(reader);	
		MultiFieldQueryParser queryparser=new MultiFieldQueryParser(Version.LUCENE_48,fields, analyzer);
		queryparser.setAllowLeadingWildcard(true);
		Query query=queryparser.parse(searchTerm.toLowerCase());
		FacetsCollector fc= new FacetsCollector();
		FacetsCollector.search(searcher, new MatchAllDocsQuery(), 10, fc);
		
		List<FacetResult> results = new ArrayList<>();
		
		Facets facets = new FastTaxonomyFacetCounts(taxoreader,config,fc);
		//results.add(facets.getSpecificValue("color", "color"));
		results.add(facets.getTopChildren(3, "color"));
		
		//System.out.println(facets.getTopChildren(numberOfHits, ))
		reader.close();
		taxoreader.close();
		
		

		return results;
		
		
	}
	

	
	
	
}
