package com.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseDao {

	/*public static void main(String[] args) {
		BaseDao b = new BaseDao();
		b.openConnection();
	}*/
	
	protected Connection conn = null;
	protected PreparedStatement ps = null;
	protected ResultSet rs = null;
	
	/**
	 * 打开数据库链接
	 */
	public void openConnection() {
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://localhost:3306/jdbc?characterEncoding=utf-8";
		String user = "root";
		String password = "123456";
		
		try {
			//加载驱动
			Class.forName(driver);
			//链接数据库
			conn = DriverManager.getConnection(url, user, password);
			System.out.println("链接数据库成功");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 关闭数据库链接
	 */
	public void closeConnection() {
		
		try {
			if(rs != null) {
				rs.close();
			}
			if(ps != null) {
				ps.close();
			}
			if(conn != null) {
				conn.close();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * 执行所有表的插入操作
		 * String
		 * StringBuffer
		 * StringBuilder
	 * @param table
	 * @param list
	 * @return
	 */
	public int insert(String table, List list) {
		int i = 0;
		//操作相对应表的sql语句
		StringBuffer sql = null;	//StringBuffer是线程安全的
		if(table.equals("userinfo")) {
			sql = new StringBuffer("insert into "+table+" values(");
		} else {
			sql = new StringBuffer("insert into "+table+" values("+list.get(0)+",");
			list.remove(0);
		}
		for(int j = 0; j<list.size(); j++) {
			if(j+1 == list.size()) {
				sql.append("?)");	//传参数结束
				break;
			}
			sql.append("?,");
		}
		openConnection();
		try {
			System.out.println(sql);
			ps = conn.prepareStatement(sql.toString());
			for (int j = 0; j < list.size(); j++) {
				ps.setObject(j+1, list.get(j));	//用object接收
			}
			i = ps.executeUpdate();	//执行成功返回1 mysql要么返回1要么返回0
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return i;	
	}
	
	/**
	 * 整体的
	 * @param sql
	 * @param colums
	 * @return
	 */
	public List query(String sql,String[] colums) {
		List list = new ArrayList();
		Map map = null;
		openConnection();
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()) {
				map = new HashMap();
				for (int i = 0; i < colums.length; i++) {
					//根据列名取数据库的值
					map.put(colums[i], rs.getObject(colums[i]));
					
				}
				list.add(map);
			}
		} catch (Exception e) {
			if(e.getMessage().equals("列名无效")) {
				System.out.println("当前查找的列名不存在");
			} else {
				e.printStackTrace();
			}
		} finally {
			closeConnection();
		}		
		
		return list;
	}
	
	/**
	 * 更新数据库，防止库存为负数
	 * @param sql
	 * @return
	 */
	public int deleteOrUpdate(String sql) {
		int i = 0;
		openConnection();
		try {
			
			ps = conn.prepareStatement(sql);
			i = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return i;
	}
	
	/**
	 * 根据bid查询库存
	 * @param bid
	 * @return
	 * @throws SQLException 
	 */
	public int queryStock(int bid) throws SQLException {
		
		int stock = 0;
		String sql = "select stock from books where bid="+bid;
		openConnection();
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
		} catch (Exception e) {
			e.printStackTrace();
		}
		rs.next();//移动游标到第一条记录
		return rs.getInt(1);
	}

}
