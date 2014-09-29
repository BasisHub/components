package com.basis.dbcomponents;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Set;

import com.basis.util.common.BBjNumber;
 

public class DataField 
{

	String Name;
	String StringValue;
	java.sql.Date DateValue;
    java.sql.Timestamp TimestampValue;
    java.sql.Time TimeValue;
    java.math.BigDecimal BigDecimalValue;
    java.lang.Double DoubleValue;
    java.lang.Integer IntegerValue;
    java.lang.Boolean BooleanValue;
    int Length;
    char Type;
    int SQLType;
    java.util.HashMap<String,String> Attributes=new java.util.HashMap<String, String>();

    public DataField(String name, char type) throws Exception
    {
    	this.Name=name;
    	setType(type);
    }

    public DataField(String name, char type, String value) throws Exception
    {
    	this(name,type);
    	setValue(value);
    }


    public DataField(String name, char type, Double doubleValue2) throws Exception {
    	this(name,type);
    	setValue(doubleValue2);
	}

	public DataField(String name, char type, Boolean booleanValue2) throws Exception {
    	this(name,type);
    	setValue(booleanValue2);
	}

	public DataField(String name, char type, Integer integerValue2) throws Exception {
    	this(name,type);
    	setValue(integerValue2);
	}

	public DataField(String name, char type, java.sql.Date dateValue2) throws Exception {
    	this(name,type);
    	setValue(dateValue2);
	}

	public DataField(String name, char type, Timestamp timestampValue2) throws Exception {
    	this(name,type);
    	setValue(timestampValue2);
	}

	public DataField(String name, char type, BigDecimal bigDecimalValue2) throws Exception {
    	this(name,type);
    	setValue(bigDecimalValue2);
	}

	public DataField(String name, char type, Time timeValue2) throws Exception {
    	this(name,type);
    	setValue(timeValue2);
	}

	public DataField(String name, char type, BBjNumber value) throws Exception {
    	this(name,type);
    	setValue(value);
	}

	public void setType(char type) throws Exception
    {
    	if ("CNDTXIBY".indexOf(type) > -1)
    	{
    		this.Type = type;
    	}
    	else
    	{
    		throw new Exception("Type unknown");
    	}
    	
    }

    public int getLength()
    {
    	return this.Length;
    }

    public void setLength(int len)
    {
    	this.Length = len;
    }

    public char getType()
    {
    	return this.Type;
    }

    public void setSQLType(int i)
    {
    	this.SQLType = i;
    }
    
    public int getSQLType()
    {
    	return this.SQLType;
    }
    
    public void setValue(String value)
    {
    	

    	if (this.Type == 'C') 
    		{
    			this.StringValue=value;
    		}
    	
    	if (this.Type == 'N') 
		{
        	if (value == null )
        	{
        		this.DoubleValue = null;
        	}
        	else
        	{
        		this.DoubleValue = java.lang.Double.valueOf(value);
        	}
		}

    	if (this.Type == 'I') 
		{
        	if (value == null )
        	{
        		this.IntegerValue = null;
        	}
        	else
        	{
        		this.IntegerValue = java.lang.Integer.valueOf(value);
        	}
        	
		}

    	if (this.Type == 'B') 
		{
        	if (value == null )
        	{
        		this.BooleanValue = null;
        	}
        	else
        	{
        		if (".T. 1 true TRUE True".contains(value))
        		{
        			this.BooleanValue = true;
        		}
        		else
        		{
        			this.BooleanValue = false;
        		}        			
        	}
        	
		}
    	
    	if (this.Type == 'D') 
		{
        	if (value == null )
        	{
        		this.DateValue = null;
        	}
        	else
        	{
        		this.DateValue = new java.sql.Date( com.basis.util.BasisDate.date(Integer.valueOf(value)).getTime() );
        	}
        	
		}    	

		if (this.Type == 'I') 
		{
			this.TimestampValue = null;
	    	if (value == null )
	    	{
	    		this.TimestampValue = java.sql.Timestamp.valueOf(value);
	    	}
	    	
		}
	
		if (this.Type == 'Y') 
		{
	    	if (value == null )
	    	{
	    		this.BigDecimalValue = null;
	    	}
	    	else
	    	{
	    		if (value == "") 
	    		{
	    			this.BigDecimalValue = new java.math.BigDecimal(0);
	    		}
	    		else
	    		{	
	    			this.BigDecimalValue = new java.math.BigDecimal(value);
	    		}
	//          rem TODO DTB
	    		
	    	}
	    	
		}
	
    }
	

    public void setValue(BBjNumber value) throws Exception
    {

        if (this.Type =='C' )
            this.StringValue=value.toString();


        if (this.Type =='N' )
        	this.DoubleValue = value.doubleValue();	

        if (this.Type =='I' )
        	this.IntegerValue =  value.intValue();

        if (this.Type =='B' )
        	this.BooleanValue = (value.compareTo(0) != 0);

        if (this.Type =='D' )
        {
            if (value.compareTo(0) == 0  )
                this.DateValue = new java.sql.Date(new java.util.Date().getTime());

            if ( value.compareTo(0) < 0 )
                this.DateValue = null;

            if ( value.compareTo(0) > 0 )
                this.DateValue = java.sql.Date.valueOf(com.basis.util.BasisDate.date(value.intValue(),0.0,"%Yl-%Mz-%Dz")); 
        }

        if (this.Type =='X' )
        {
            if ( value.equals(0) )
                this.TimestampValue = new java.sql.Timestamp(new java.util.Date().getTime());

            if ( value.compareTo(0) < 0 )
                this.TimestampValue = null;

            if ( value.compareTo(0)>0 )
                this.TimestampValue = java.sql.Timestamp.valueOf(com.basis.util.BasisDate.date(value.intValue(),0.0,"%Yl-%Mz-%Dz"));
                
        }


        if (this.Type =='Y' )
            this.BigDecimalValue = new java.math.BigDecimal(value.doubleValue());


        if (this.Type =='T' )
            this.TimeValue = new java.sql.Time(value.intValue());

    }
    
	public void setValue(java.sql.Date value)
	{
			this.DateValue = value;
	}

	public void setValue(java.sql.Timestamp value)
	{
		this.TimestampValue=value;
	}

	public void setValue(java.lang.Double value)
	{
		this.DoubleValue=value;
	}
	
	public void setValue(java.lang.Integer value)
	{
		this.IntegerValue=value;
	}
	
	public void setValue(java.lang.Boolean value)
	{
		this.BooleanValue=value;
	}

	public void setValue(java.math.BigDecimal value)
	{
		this.BigDecimalValue=value;
	}

	public void setValue(java.sql.Time value)
	{
		TimeValue=value;
	}

	public String getValueAsString()
	{
		String ret="";
      
		if (this.Type=='C') 
		{
		if (this.StringValue == null)
			{
				ret="";
			}
			else
			{
				ret=this.StringValue;
			}
		}
  
		if (this.Type =='N' )
			ret=this.DoubleValue.toString();

		if (this.Type =='I' )
			ret=this.IntegerValue.toString();
  
		if (this.Type == 'B' )
			if (this.BooleanValue)
				ret="1";
			else
				ret="0";

		if (this.Type == 'D' )
		{
			//	TODO: nice formatting honoring locale
			ret=this.DateValue.toString();
		}
		if (this.Type =='X' )
		{
			//TODO: nice formatting honoring locale
			ret=this.TimestampValue.toString();
		}
		if (this.Type == 'Y' )
		{
			//TODO: nice formatting honoring locale
			ret=this.BigDecimalValue.toString();
		}
		if (this.Type == 'T' )
		{
			//TODO: nice formatting honoring locale
			ret=this.TimeValue.toString();
		}
  	
		return ret;
	}

	public Boolean isNull()
	{

        if (this.Type == 'C' && this.StringValue != null)
        	return false;

        if (this.Type == 'N' && this.DoubleValue != null )
        		return false;

        if (this.Type =='I' && this.IntegerValue != null )
        		return false;

        if (this.Type =='B' && this.BooleanValue != null )
                return false;

        if (this.Type =='D' && this.DateValue != null )
        	return false;

        if (this.Type =='X' && this.TimestampValue != null )
        	return false;
        
        if (this.Type =='Y' && this.BigDecimalValue != null )
        	return false;

        if (this.Type =='T' && this.TimeValue != null )
        	return false;

        return true;
	}

	public String getValueForSQL()
	{
		String ret="";
		
        if (this.Type =='C' )
            if ( this.StringValue == null )
                ret = "NULL";
            else
                ret = "'"+this.StringValue+"'";

        if (this.Type == 'N' )
            if ( this.DoubleValue == null )
                ret = "NULL";
            else
                ret = this.DoubleValue.toString();

        if (this.Type =='I' )
            if ( this.IntegerValue == null )
                ret = "NULL";
            else
                ret = this.IntegerValue.toString();

        if (this.Type =='B' )
            if ( this.BooleanValue == null )
                ret = "NULL";
            else
                ret=this.BooleanValue.toString();

        if (this.Type =='D' )
            if ( this.DateValue == null )
                ret = "NULL";
            else
                ret=this.DateValue.toString();

        if (this.Type =='X' )
            if ( this.TimestampValue == null )
                ret = "NULL";
            else
                ret = "'"+this.TimestampValue.toString()+"'";

        if (this.Type =='Y' )
            if ( this.BigDecimalValue == null )
                ret = "NULL";
            else
                ret=this.BigDecimalValue.toString();

        if (this.Type =='T' )
            if ( this.TimeValue == null )
                ret = "NULL";
            else
                ret=this.TimeValue.toString();

		return ret;
	}
	
	public Object getObject()
	{
		Object obj=null;

        if (this.Type =='C' )
            obj = this.StringValue;

        if (this.Type =='N' )
            obj = this.DoubleValue;

        if (this.Type =='I' )
            obj = this.IntegerValue;

        if (this.Type =='B' )
            obj = this.BooleanValue;

        if (this.Type =='D' )
            obj = this.DateValue;

        if (this.Type =='X' )
            obj = this.TimestampValue;

        if (this.Type =='Y' )
            obj = this.BigDecimalValue;

        if (this.Type =='T' )
            obj = this.TimeValue;
		
		return obj;
	}

	public Double getValueAsNumber() throws Exception
	{
		Double ret = 0.0;

        if (this.Type =='C' )
            if ( this.StringValue!=null )  
            	ret=Double.valueOf(this.StringValue); 
            else 
            	ret = 0.0;

        if (this.Type =='N' )
            ret=this.DoubleValue;

        if (this.Type =='D' )
            if ( this.DateValue == null )
                ret=-1.0;
            else
            {
                String ret1=this.DateValue.toString();
                Integer ret2=com.basis.util.BasisDate.jul(Integer.valueOf(ret1.substring(0,3)),Integer.valueOf(ret1.substring(5,6)), Integer.valueOf(ret1.substring(8,9)));
                ret=ret2.doubleValue();
            }

        if (this.Type =='X' )
            if ( this.TimestampValue==null )
                ret=-1.0;
            else
            {
                Integer ret2=com.basis.util.BasisDate.jul(this.TimestampValue.getYear(),this.TimestampValue.getMonth(), this.TimestampValue.getDay());
        		ret=ret2.doubleValue();
            }

        if (this.Type =='B' )
            if ( this.BooleanValue )
                ret=1.0;
            else
                ret=0.0;

        if (this.Type =='I' )
            if ( this.IntegerValue==null )
                ret=-1.0;
            else
                ret=this.IntegerValue.doubleValue();

        if (this.Type =='Y' )
            if ( this.BigDecimalValue==null )
                ret=-1.0;
            else
                ret=this.BigDecimalValue.doubleValue();

        if (this.Type =='T' )
            if ( this.TimeValue==null )
                ret=-1.0;
            else
            {
            	Integer ret2=com.basis.util.BasisDate.jul(this.TimeValue.getYear(),this.TimeValue.getMonth(), this.TimeValue.getDay());
            	ret=ret2.doubleValue();
            }                                   

		return ret;
	}
	
	public void setAttribute(String name, String value)
	{
		this.Attributes.put(name,value);
	}

	public String getAttribute(String name)
	{
		return this.Attributes.get(name);
	}

	@SuppressWarnings("unchecked")
	public java.util.HashMap<String,String> getAttributes()
	{
		java.util.HashMap<String,String> clone = (java.util.HashMap<String,String>)this.Attributes.clone();
		return clone;
	}

	public void removeAttribute(String name)
	{
		this.Attributes.remove(name);
	}


	public DataField clone() 
	{
		DataField f=null;
		try {

        if ( this.Type == 'N' )
				f = new DataField(this.Name,this.Type,this.DoubleValue);
		else
            f = new DataField(this.Name,this.Type,this.StringValue);

        if ( this.Type == 'B' )
            f = new DataField(this.Name,this.Type,this.BooleanValue);

        if ( this.Type == 'I' )
            f = new DataField(this.Name,this.Type,this.IntegerValue);

        if ( this.Type == 'D' )
            f = new DataField(this.Name,this.Type,this.DateValue);

        if ( this.Type == 'X' )
        	f = new DataField(this.Name,this.Type,this.TimestampValue);

        if ( this.Type == 'Y' )
        	f = new DataField(this.Name,this.Type,this.BigDecimalValue);
 
        if ( this.Type == 'T' )
        	f = new DataField(this.Name,this.Type,this.TimeValue);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

       // TODO types TDB

        if (f != null)
		{
	        Set<String> ks = this.Attributes.keySet();
	        Iterator<String> it = ks.iterator();
	
	        while (it.hasNext())
	        {
	            String k = it.next();
	            String v = this.Attributes.get(k);
	            f.setAttribute(k,v);
	        }
		}
		
		return f;
	}	
}
