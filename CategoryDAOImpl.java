import java.util.List;

import javax.persistence.NoResultException;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import com.org.o7planning.webshoppingcart.dao.CategoryDAO;
import com.org.o7planning.webshoppingcart.entity.Category;
import com.org.o7planning.webshoppingcart.entity.Product;
import com.org.o7planning.webshoppingcart.model.CategoryInfo;
import com.org.o7planning.webshoppingcart.model.CategoryResult;
import com.org.o7planning.webshoppingcart.model.ProductInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CategoryDAOImpl implements CategoryDAO {

	@Autowired
	private SessionFactory sessionFactory;
	private Session session;
	
	public Category findCategoryByName(String categoryName) {
		//session = getSession();
		Criteria crit = sessionFactory.getCurrentSession().createCriteria(Category.class);
		crit.add(Restrictions.eq("categoryName", categoryName));
		return (Category)crit.uniqueResult();
	}
	
	public CategoryInfo findCategoryInfoByID(String id) {
		
		Category category = this.findCategoryByID(id);
		return new CategoryInfo(category);
	}
	
	public List<Category> getCategorys(){
		@SuppressWarnings("unchecked")
		List<Category> categorys = sessionFactory.getCurrentSession().createQuery("from Category").list();
		return categorys;
	}
	
	public Category findCategoryByCode(String code) {
		Criteria crit = sessionFactory.getCurrentSession().createCriteria(Category.class);
		crit.add(Restrictions.eq("code", code));
		return (Category)crit.uniqueResult();
	}

	public void saveCategory(CategoryInfo categoryInfo) {
		String id = categoryInfo.getId();
		Category category = null;
		boolean isNew = false;
		if(id != null) {
			category = this.findCategoryByID(id);
		}
		if(category == null) {
			isNew = true;
			category = new Category();
		}
		category.setId(id);
		category.setCode(categoryInfo.getCode());
		category.setCategoryName(categoryInfo.getCategoryName());
		if(isNew) {
			this.sessionFactory.getCurrentSession().persist(category);
		}
		
		this.sessionFactory.getCurrentSession().flush();

	}
	
	public CategoryResult<Category> queryCategorys(int page, int maxResult, int maxNavigationPage,
			String likeName){
		String sql = "Select new " + Category.class.getName() //
                + "(c.id, c.code, c.categoryName) " + " from "//
                + Category.class.getName() + " c ";
        if (likeName != null && likeName.length() > 0) {
            sql += " Where lower(c.categoryName) like :likeName ";
        }
		session = this.sessionFactory.getCurrentSession();
		//String sql = "from Category";
		Query query = session.createQuery(sql);
		if (likeName != null && likeName.length() > 0) {
            query.setParameter("likeName", "%" + likeName.toLowerCase() + "%");
        }
		return new CategoryResult(query, page, maxResult, maxNavigationPage); 
	}
	
	public Category findCategoryByID(String id) {
		Session session = sessionFactory.getCurrentSession();
		Criteria crit = session.createCriteria(Category.class);
		crit.add(Restrictions.eq("id", id));
		return (Category)crit.uniqueResult();
	}
	
	private Session getSession() {
		if(session == null) {
			session = sessionFactory.getCurrentSession();
		}
		return session;
	}

}
