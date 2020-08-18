package com.basiscomponents.rest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class MultipartPost {

	    private final String boundary;
	    private static final String LF = "\r\n";
	    private HttpURLConnection httpconn;
	    private String charset;
	    private OutputStream outstream;
	    private PrintWriter pwriter;

	    /**
	     * create a new HTTP POST request with content multipart/form-data
	     *
	     * @param requestURL
	     * @param charset
	     */
	    public MultipartPost(String requestURL, String charset, Boolean ignoreSSLError)
	            throws IOException, KeyManagementException, NoSuchAlgorithmException {

	        if (ignoreSSLError && requestURL.startsWith("https://")) {
	        
				 // Create a trust manager that does not validate certificate chains
			     TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
			             public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			                 return null;
			             }
			             public void checkClientTrusted(X509Certificate[] certs, String authType) {
			             }
			             public void checkServerTrusted(X509Certificate[] certs, String authType) {
			             }
			         }
			     };
			
			     SSLContext sc = SSLContext.getInstance("SSL");
			     sc.init(null, trustAllCerts, new java.security.SecureRandom());
			     HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			
			     HostnameVerifier allHostsValid = new HostnameVerifier() {
			         public boolean verify(String hostname, SSLSession session) {
			             return true;
			         }
			     };

	     		// Install the host verifier that trusts all certs
	     		HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);        
	     		
	        }
	        this.charset = charset; 
	     	// creates a unique boundary based on time stamp
	     	boundary = "===" + System.currentTimeMillis() + "===";
	     	URL url = new URL(requestURL);
	     	httpconn = (HttpURLConnection) url.openConnection();        
	        httpconn.setUseCaches(false);
	        httpconn.setDoOutput(true);    // indicates POST method
	        httpconn.setDoInput(true);
	        httpconn.setRequestProperty("Content-Type","multipart/form-data; boundary=" + boundary);
	        outstream = httpconn.getOutputStream();
	        pwriter = new PrintWriter(new OutputStreamWriter(outstream, charset),true);
	    }

	    /**
	     * Adds a form field to the request
	     *
	     * @param name  field name
	     * @param value field value
	     */
	    public void addFormField(String name, String value) {
	        pwriter.append("--" + boundary).append(LF);
	        pwriter.append("Content-Disposition: form-data; name=\"" + name + "\"")
	                .append(LF);
	        pwriter.append("Content-Type: text/plain; charset=" + charset).append(
	                LF);
	        pwriter.append(LF);
	        pwriter.append(value).append(LF);
	        pwriter.flush();
	    }

	    /**
	     * Adds a upload file section to the request
	     *
	     * @param fieldName  name attribute in <input type="file" name="..." />
	     * @param uploadFile a File to be uploaded
	     * @throws IOException
	     */
	    public void addFilePart(String fieldName, File uploadFile)
	            throws IOException {
	        String fileName = uploadFile.getName();
	        pwriter.append("--" + boundary).append(LF);
	        pwriter.append(
	                "Content-Disposition: form-data; name=\"" + fieldName
	                        + "\"; filename=\"" + fileName + "\"")
	                .append(LF);
	        pwriter.append(
	                "Content-Type: "
	                        + URLConnection.guessContentTypeFromName(fileName))
	                .append(LF);
	        pwriter.append("Content-Transfer-Encoding: binary").append(LF);
	        pwriter.append(LF);
	        pwriter.flush();

	        FileInputStream inputStream = new FileInputStream(uploadFile);
	        byte[] buffer = new byte[4096];
	        int bytesRead = -1;
	        while ((bytesRead = inputStream.read(buffer)) != -1) {
	            outstream.write(buffer, 0, bytesRead);
	        }
	        outstream.flush();
	        inputStream.close();
	        pwriter.append(LF);
	        pwriter.flush();
	    }

	    /**
	     * Adds a header field to the request.
	     *
	     * @param name  - name of the header field
	     * @param value - value of the header field
	     */
	    public void addHeaderField(String name, String value) {
	        pwriter.append(name + ": " + value).append(LF);
	        pwriter.flush();
	    }

	    /**
	     * Completes the request and receives response from the server.
	     *
	     * @return a list of Strings as response in case the server returned
	     * status OK, otherwise an exception is thrown.
	     * @throws IOException
	     */
	    public List<String> finish() throws IOException {
	        List<String> response = new ArrayList<String>();
	        pwriter.append(LF).flush();
	        pwriter.append("--" + boundary + "--").append(LF);
	        pwriter.close();

	        // checks server's status code first
	        int status = httpconn.getResponseCode();
	        if (status >= 200 && status <= 299) {
	            BufferedReader reader = new BufferedReader(new InputStreamReader(
	                    httpconn.getInputStream()));
	            String line = null;
	            while ((line = reader.readLine()) != null) {
	                response.add(line);
	            }
	            reader.close();
	            httpconn.disconnect();
	        } else {
	        	
	        	InputStream stream = httpconn.getErrorStream();
	            StringBuilder textBuilder = new StringBuilder();
	            try (Reader reader = new BufferedReader(new InputStreamReader
	              (stream))) {
	                int c = 0;
	                while ((c = reader.read()) != -1) {
	                    textBuilder.append((char) c);
	                }
	                httpconn.disconnect();
	            throw new IOException("Error : "+textBuilder.toString());
	            }
	            
	        }
	        
	        
	        return response;
	    }
	
	
}
