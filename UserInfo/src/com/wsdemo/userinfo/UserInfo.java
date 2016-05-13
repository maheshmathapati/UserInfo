package com.wsdemo.userinfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.wsdemo.connectionUtil.ConnectionUtil;
import com.wsdemo.connectionUtil.DocumentUtil;

@Path("userinfo")
public class UserInfo {

	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Response getUserInfo() throws ParserConfigurationException, TransformerException, SQLException
	{
		Document doc = DocumentUtil.newDocument();
		Element rootElem = doc.createElement("root");
		doc.appendChild(rootElem);
		try(
				Connection conn = ConnectionUtil.getConnection();
				Statement stmt = conn.createStatement(
						ResultSet.TYPE_SCROLL_INSENSITIVE, 
						ResultSet.CONCUR_READ_ONLY);
				ResultSet rset = stmt.executeQuery("SELECT * FROM userinfo");
				)
				{
			while(rset.next())
			{
				Element userInfo = doc.createElement("userInfo");
				rootElem.appendChild(userInfo);
				int id = rset.getInt("id");
				String name = rset.getString("name");
				String number = rset.getString("number");
				String address = rset.getString("address");
				
				userInfo.setAttribute("id", Integer.toString(id));
				Element nameElem = doc.createElement("name");
				Element numberElem = doc.createElement("number");
				Element addressElem = doc.createElement("address");
				
				nameElem.setTextContent(name);
				numberElem.setTextContent(number);
				addressElem.setTextContent(address);
				userInfo.appendChild(nameElem);
				userInfo.appendChild(numberElem);
				userInfo.appendChild(addressElem);
			}
			
	        return Response.ok(DocumentUtil.toString(doc)).build();
		}
		
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	public Response createUser(@Context HttpServletRequest req,
			@QueryParam("name") String name,
			@QueryParam("number") String number)  
					throws IOException, ParserConfigurationException, SAXException
	{
		
		String address = readContent(req);
		address = address.trim();
		String insertCommand = 
				"INSERT INTO userinfo (name, number, address) VALUES (?, ?, ?)";
		try(
				Connection conn = ConnectionUtil.getConnection();
				PreparedStatement stmt = conn.prepareStatement(insertCommand,
						Statement.RETURN_GENERATED_KEYS);
				)
				{
			stmt.setString(1, name);
			stmt.setString(2, number);
			stmt.setString(3, address);
			stmt.executeUpdate();
				}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return Response.ok().build();
	}
	
    private static String readContent(HttpServletRequest req)
    throws IOException
    {
        String read ="";
        String lineSeperator = System.getProperty("line.separator");
        StringBuilder sb=new StringBuilder();
        if(req.getContentLength()>0){
            InputStreamReader is =
                new InputStreamReader(req.getInputStream());
            BufferedReader br = new BufferedReader(is);
            read = br.readLine();

            while(read != null) {
                sb.append(read);
                sb.append(lineSeperator);
                read =br.readLine();
            }
            read = sb.toString();
        }
        read = (read!=null)?read:"";
        return read;
    }

}
