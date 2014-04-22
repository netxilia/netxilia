<html>
<body>
<div class="readme">
<h2>Welcome to Netxilia</h2>
<p>
	Netxilia is an open source online spreadsheet application written completely in Java. 
	The client interface is built using HTML and Javascript. 
</p>
<p> 
	Netxilia is focused on collaborative access to spreadsheets. It offers most of the key functionalities present 
	in applications like Microsoft Excel or Google Spreadsheets. 
	To improve the performance and to increase the limits of the spreadsheet storage, 
	data is stored in regular databases in an easy-to-read format.  
</p>

<!-- ######################################### -->
<h2>Where can you use Netxilia?</h2>

<ul>
	<li>Netxilia can be used as-is as a spreadsheet application for your organization. 
		Being open source we'll encourage the community to contribute to enrich its functionality 
		(new functions and templates) for a wide variety of purposes.</li>
	<li>Netxilia can be used as an enhanced database viewer/editor. 
		Netxilia's storage is a regular relational database. 
		It tries to accommodate a given table structure. 
		The data that differentiate a spreadsheet from a relational table - basically formatting and formulas - 
		are stored in a separate table linked to the data table.</li>
	<li>Netxilia can be embedded as an administration console for database intensive applications. 
		Thus developers are free to concentrate on the client interface and less on the administration interface.</li>
	<li>As all the spreadsheets' data can be accessible via REST/JSON, one can develop web applications in any 
		programming language using Netxilia as a back-end. Business rules can be very easily defined this way.</li>
</ul>

<!-- ######################################### -->
<h2>Why Netxilia?</h2>
<p>
	Spreadsheets are a great way to express business rules. Even though in the commercial world or free (but closed source) 
	there are well-established leaders, in the open source world the situation is less established. 
	Netxilia is a completely autonomous system using either internal or external relational database. 
	There are several open-source applications similar to Netxilia, but very few developed in Java. 
	The programming language is important as we see Java as one of the most important language for the open source 
	business applications. And we believe the key to the success of an open source project is its ability to be extensible
	 and to attract contributors.
</p>

<!-- ######################################### -->
<h2>Features</h2>
<p>
	Netxilia already has numerous functionalities found in the spreadsheet applications you're already used to.
	We chose to implement in the first version those functionalities that made most sense to us for a regular usage.
	We're committed to add the missing functionalities based on the community demands. So if you'd like to start using Netxilia,
	but some key functionalities missing prevent you to do so, drop us a line at <a href="mailto:netxilia@netxilia.org">netxilia@netxilia.org</a>
	and we'll do our best to integrate your ideas in our next releases.
</p> 

<p>
	So here is a non-exhaustive list of Netxilia's functionalities:
</p>
<ul>
	<li>supports Excel-like formulas</li>
	<li>inline formula edit, formatting, styling</li>
	<li>inter-sheet references</li>
	<li>around 150 functions in the mathematical, statistical, date, number and text treatment</li>
	<li>line, bar flash charts</li>
	<li>Excel import/export</li>
	<li>JSON import/export</li>
	<li>PDF export and print</li>
	<li>sorting, filtering</li>
	<li>simple REST API to access spreadsheets and cells</li>
	<li>multiuser simultaneous edit</li>
	<li>range names (aliases)</li>
</ul>


<!-- ######################################### -->
<h2>Original functionalities</h2>
<p>
Not depending on an existing library (e.g. POI) or storage format (e.g. Excel file), we have the freedom to propose functionalities
that are not (yet ;) present into the other solutions.
Even though the interface may look familiar to you, there are some differences that may improve greatly
the way you work and share your work inside your organization.
</p>
<ul>
	<li><b>The workbook</b> as you're used to is a collection of several spreadsheets that can easily communicate with each other.
	As Netxilia's focus is on organization-scale spreadsheet system, the workbook in Netxilia's acceptance is a collection of tens, hundreds of spreadsheets sharing data.
	What they have in common is that they are stored in the same database. Thus when you open an entry in Netxilia, you open a <u>spreadsheet</u> only, not an <u>entire workbook</u>!.  
	</li>
	<li><b>Spreadsheet edit</b> interface focus both on performance and collaboration. When you open the spreadsheet editor instead of one table you have three! 
		<ol>
			<li>The main one is for the spreadsheet's regular data.</li> 
			<li>The bottom one contains different calculations (totals) based on the regular data and other footers. 
			This allow you not only to have all the time in view the totals when you work with big quantity of data but also to improve the storage performance 
			(keeping potentially different data types in separate database tables).</li> 
			<li>And finally a floating table is a mini-spreadsheet that accompanies the main spreadsheet, but it's private to each user.</li>
		</ol>
	</li>  
	<li>The treeview. Click on the "tree" button and Netxilia will add a "expand/collapse" button whenever a row is indented compared 
	to the next rows</li>
	<li>The auto-insert row - when toggled, whenever you press Enter a new row is inserted under the current row and the cursor moves to this new row. 
	This allows you to quickly insert data in the middle of the spreadsheet.</li>
	<li>The quick filter - click filter button (or F7) in a cell and Netxilia will keep only the rows having 
		 on the same column a value equal to the selected cell's value.</li>
	<li>The formula filter - click formula filter button in a cell and Netxilia will keep only the rows 
		for which the formula from the selected cell is true.</li>
	
	<li>Integrated user and permission management. Netxilia focuses on multiple user environments. 
		To allow organization to quickly start using the application, an easy-to-use user and permission management tools is included. In the <i>SYSTEM</i> workbooks there is a spreadsheet containing all the users in the system 
		their password and their role (regular user or administrator). Then in each workbook, a special spreadsheet called <i>permissions</i> contains for each sheet who can read it and write it.</li>

	<li>CSS based style management. To style spreadsheets, Netxilia leverages the CSS technology. This is quite easy to use: a special spreadsheet called <i>styles</i> can be included in any workbook.
	In this spreadsheet you can define style names and their definition (you can even re-define built-in styles like <i>b</i> that is used for bold!). Then you can use the different style functions for value-based styling or
	you can set directly a style you defined. </li>
	
	<li>Control cell's look via formulas. As described before you can use formulas to change a cell's styling dynamically or the text displayed in the cell. See more on DISPLAYINTERVALS, DISPLAYDECODE, STYLEINTERVALS, STYLEDECODE functions</li>
	
	<li>Advanced aliases: the edition interface lets you easily define an alias for a column (a textbox is present under each column header)
		In a formula you can then reference a given cell from that column. E.g. let's say column C is named "Quantity". A <i>formula = Quantity 2 + 3</i> references the 2nd row from this column.</li>
		
	<li>Quick cell edit. It happens sometimes to have long formulas inside the cells, but regularly you only change one parameters of this formulas. What you do is double-click (or F2) on the cell, move the cursor close to your number and change it.
	Imagine you have a formula like: <i>=-PV(D16, D13/12, D18) + 10*2</i> and you want to quickly change only the 2nd parameter. By simply surrounding it with parentheses <i>=-PV(D16, D13/12, D18) + 10*<b>(</b>2<b>)</b></i>
	whenever you type a number in this cell, only that part of the formula will be change. You can still modify the formula by double-clicking in the cell!
	</li>
</ul>

<!-- ######################################### -->
<h2>Technology</h2>
<p>Netxilia is built with the help of commonly used high performance Java and Javascript libraries. 
	We'd like to say a special thank to all these folks who built these nice pieces of technology.</p>
<ul>
	<li>Java 1.6+</li>
	<li>Spring</li>
	<li>Jersey JAX-RS</li>
	<li>jakarta common libraries</li>
	<li>Joda time</li>
	<li>log4j/slf4j</li>
	<li>POI - for excel export</li>
	<li>itext - for PDF</li>
	<li>Google collections</li>
	<li>Gson</li>
	<li>ehcache</li>

	<li>Jquery</li>
	<li>different jquery plugins like form, validate, treeview, ...</li>
	
	<li>maven</li>
</ul>

<p>
	Netxilia was successfully tested with H2, Derby, PostgreSQL, MySQL.
</p>
<!-- ######################################### -->
<h2>License</h2>
<p>Netxilia is licensed under <a href="http://www.gnu.org/licenses/lgpl.html">LGPL v3</a> license.</p>
</div>
</body>
</html>
