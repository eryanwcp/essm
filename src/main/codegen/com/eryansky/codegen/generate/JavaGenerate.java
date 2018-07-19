package com.eryansky.codegen.generate;

import com.eryansky.codegen.util.FileType;
import com.eryansky.codegen.util.FileUtil;
import com.eryansky.codegen.util.Resources;
import com.eryansky.codegen.util.VelocityUtil;
import com.eryansky.codegen.vo.Table;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class JavaGenerate implements Generate {
	FileType javaFileType = null;

	public JavaGenerate(FileType javaFileType) {
		this.javaFileType = javaFileType;
	}

	@Override
	public void generate(Table table) throws Exception {
		if (javaFileType == null)
			throw new Exception(JavaGenerate.class.getName() + ": JavaFileType 为null");
		/* get the Template */
		Template t = new VelocityUtil().getTemplate(Resources.TEMPLATE_PATH+"/"+javaFileType.getTemplate());
		/* create a context and& nbsp;add data */
		VelocityContext context = new VelocityContext();
		context.put("entityPackage", Resources.getPackage(FileType.ENTITY));
		context.put("daoPackage", Resources.getPackage(FileType.DAO));
		context.put("servicePackage", Resources.getPackage(FileType.SERVICE));
		context.put("controllerPackage", Resources.getPackage(FileType.CONTROLLER));
		String entityInstance=table.getEntityName().substring(0,1).toLowerCase()+table.getEntityName().substring(1);
		context.put("entityName", table.getEntityName());
		context.put("entityInstance", entityInstance);
		context.put("tableName", table.getTableName());
		context.put("tableComment", table.getRemark());
		context.put("columns", table.getColumns());
		context.put("requestMapping", Resources.REQUEST_MAPPING);
		context.put("module", Resources.MODULE);//模块名
		context.put("modulePath", Resources.MODULE.replace(".","/"));//路径
		context.put("modulePermission", Resources.MODULE.replace(".",":"));//权限
		context.put("date", new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));
		context.put("year", new SimpleDateFormat("yyyy").format(Calendar.getInstance().getTime()));
		context.put("author", Resources.AUTHOR);
		context.put("productName", Resources.PRODUCT_NAME);
		context.put("productUrl", Resources.PRODUCT_URL);
		/* now render the template into a StringWriter */
		StringWriter writer = new StringWriter();
		t.merge(context, writer);
		FileUtil.create(Resources.getJavaStorePath(javaFileType), table.getEntityName() + javaFileType.getFileNameExtension(), writer.toString());
	}

	@Override
	public void generate(List<Table> tables) throws Exception {

	}

}
