package util;

import org.springframework.dao.ConcurrencyFailureException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ExclusiveReadLockManager {


    public static boolean acquireLock(int lockable, String owner) throws ConcurrencyFailureException{

        if(!hasLock(lockable,owner)){
            try(Connection con = DatabaseConnection.getConnection()){

                PreparedStatement ps = con.prepareStatement("INSERT INTO `lock`(lockable, owner)VALUES (?,?)");
                ps.setInt(1,lockable);
                ps.setString(2,owner);
                int i=ps.executeUpdate();
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                throw new ConcurrencyFailureException("unable to lock");
            }
        }
        return true;
    }
    public static void releaseLock(int lockable,String owner){

            try(Connection con = DatabaseConnection.getConnection()){

                PreparedStatement ps = con.prepareStatement("DELETE FROM `lock` WHERE lockable = ? AND owner = ?");
                ps.setInt(1,lockable);
                ps.setString(2,owner);
                ps.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }

    }

    public static boolean hasLock(int lockable, String owner) {
        int judge=0;
        try (Connection con = DatabaseConnection.getConnection()) {
            // ...（数据库连接代码）
            PreparedStatement ps = con.prepareStatement("SELECT lock_id FROM `lock` WHERE lockable = ? AND owner = ?");
            ps.setInt(1, lockable);
            ps.setString(2, owner);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                judge=rs.getInt("lock_id");
            }

            if(judge==0){
                return false;
            }else{
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
