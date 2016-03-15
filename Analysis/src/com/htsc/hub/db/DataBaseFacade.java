package com.htsc.hub.db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.log4j.Logger;


public class DataBaseFacade
{

    private static Logger logger = Logger.getLogger(DataBaseFacade.class);

    private static final DataBaseFacade instance = new DataBaseFacade();

    private static SqlSessionFactory sessionFactory;

    private DataBaseFacade()
    {
        try
		{	
			String resource = "mybatis-config.xml";
			InputStream inputStream = Resources.getResourceAsStream(resource);
			if(inputStream!=null){
				System.out.println("初始化inputStream成功");
			}else{
				System.out.println("初始化inputStream失败");
			}
			sessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
			if(sessionFactory!=null){
				System.out.println("初始化MyBatis成功");
			}else{
				System.out.println("初始化MyBatis失败");
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
    }

    public static DataBaseFacade getInstance()
    {
        return instance;
    }

    public int updateRow(String sqlId, Object o)
    {
        if (null == sqlId || null == o)
        {
            return 0;
        }
        int ret = 0;
        SqlSession session = sessionFactory.openSession();
        try
        {
            long t = System.currentTimeMillis();
            ret = session.update(sqlId, o);
            if (logger.isInfoEnabled())
            {
                logger.info("sqlId=" + sqlId + "；耗时"
                        + (System.currentTimeMillis() - t) + "ms");
            }
            session.commit();
        }
        catch (RuntimeException e)
        {
            System.out.println("更新数据库时发生运行时异常，sqlId=" + sqlId);
            System.out.println(e);
        }
        finally
        {
            session.close();
        }
        return ret;
    }

    public int insertRow(String sqlId, Object o)
    {
        if (null == sqlId || null == o)
        {
            return -1;
        }
        int ret = -1;
        SqlSession session = sessionFactory.openSession();
        try
        {
            long t = System.currentTimeMillis();
            ret = session.insert(sqlId, o);
            if (logger.isInfoEnabled())
            {
                logger.info("sqlId=" + sqlId + "；耗时"
                        + (System.currentTimeMillis() - t) + "ms");
            }
            session.commit();
        }
        catch (RuntimeException e)
        {
            System.out.println("插入数据库时发生运行时异常，sqlId=" + sqlId);
            System.out.println(e);
        }
        finally
        {
            session.close();
        }
        return ret;
    }
    
    public int transOperate(String[] sqlArray, Object[] objArray, String[] methodArray)
    {
        if (objArray.length != sqlArray.length || methodArray.length != sqlArray.length || sqlArray.length==0)
        {
            return -1;
        }
        int ret = 0;
        SqlSession session = sessionFactory.openSession();
        String sqlId = "";
        try
        {
            long t = System.currentTimeMillis();
            for(int i=0;i<sqlArray.length;i++){
            	sqlId = sqlId + sqlArray[i];
            	if(methodArray[i].equals("insert")){
            		ret = ret + session.insert(sqlArray[i], objArray[i]);
            	}else if(methodArray[i].equals("delete")){
            		ret = ret + session.delete(sqlArray[i], objArray[i]);
            	}else if(methodArray[i].equals("update")){
            		ret = ret + session.update(sqlArray[i], objArray[i]);
            	}
            	sqlId = sqlArray[i] + "|";
            }
            if (logger.isInfoEnabled())
            {
                logger.info("sqlId=" + sqlId + "；耗时"
                        + (System.currentTimeMillis() - t) + "ms");
            }
            session.commit();
        }
        catch (RuntimeException e)
        {
            System.out.println("操作数据库时发生运行时异常，sqlId=" + sqlId);
            System.out.println(e);
        }
        finally
        {
            session.close();
        }
        return ret;
    }

    public int deleteAndInsert(String deleteID, Object d, String insertID,
            Object o)
    {
        if (null == deleteID || null == o || null == insertID)
        {
            return -1;
        }
        int ret = -1;
        SqlSession session = sessionFactory.openSession();
        session.commit(false);

        try
        {
            long t = System.currentTimeMillis();
            ret = session.delete(deleteID, d);
            ret = session.insert(insertID, o);

            if (logger.isInfoEnabled())
            {
                logger.info("sqlId=" + insertID + "；耗时"
                        + (System.currentTimeMillis() - t) + "ms");
            }
            session.commit();
        }
        catch (Exception e)
        {
            System.out.println("先删除再插入出现异常，sqlId=" + deleteID + " ," + insertID);
            System.out.println(e);
            session.rollback();
        }
        finally
        {
            session.close();
        }

        return ret;
    }

    public int deleteRow(String sqlId, Object o)
    {
        if (null == sqlId || null == o)
        {
            return -1;
        }
        int ret = -1;
        SqlSession session = sessionFactory.openSession();
        try
        {
            long t = System.currentTimeMillis();
            ret = session.delete(sqlId, o);
            if (logger.isInfoEnabled())
            {
                logger.info("sqlId=" + sqlId + "；耗时"
                        + (System.currentTimeMillis() - t) + "ms");
            }
            session.commit();
        }
        catch (RuntimeException e)
        {
        	e.printStackTrace();
            System.out.println("删除时发生运行时异常，sqlId=" + sqlId);
            System.out.println(e);
        }
        finally
        {
            session.close();
        }
        return ret;
    }

    @SuppressWarnings("unchecked")
    public <T> T getRow(String sqlId, Object example, T defValue)
    {
        if (null == sqlId || null == example)
        {
            return defValue;
        }
        long t = System.currentTimeMillis();
        SqlSession session = null;
        try
        {
            session = sessionFactory.openSession();
            T ret = (T) session.selectOne(sqlId, example);
            if (logger.isInfoEnabled())
            {
//                logger.info("sqlId=" + sqlId + "；耗时"
//                        + (System.currentTimeMillis() - t) + "ms");
            }
            if (null != ret)
            {
                return ret;
            }
        }
        catch (RuntimeException e)
        {
            System.out.println("查询数据库时发生运行时异常，sqlId=" + sqlId);
            System.out.println(e);
        }
        finally
        {
            if (null != session)
            {
                session.close();
            }
        }
        return defValue;
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getRowList(String sqlId, Object example, RowBounds rb)
    {
        if (null == sqlId || null == example)
        {
            return new ArrayList<T>();
        }
        long t = System.currentTimeMillis();
        SqlSession session = null;
        try
        {
            session = sessionFactory.openSession();
            List<T> ret = null;
            if (null == rb)
            {
                ret = session.selectList(sqlId, example);
            }
            else
            {
                ret = session.selectList(sqlId, example, rb);
            }
            if (logger.isInfoEnabled())
            {
//                logger.info("sqlId=" + sqlId + "；耗时"
//                        + (System.currentTimeMillis() - t) + "ms");
            }
            return ret;
        }
        catch (RuntimeException e)
        {
        	e.printStackTrace();
            System.out.println("查询数据库时发生运行时异常，sqlId=" + sqlId);
            System.out.println(e);
        }
        finally
        {
            if (null != session)
            {
                session.close();
            }
        }
        return new ArrayList<T>();
    }

}
