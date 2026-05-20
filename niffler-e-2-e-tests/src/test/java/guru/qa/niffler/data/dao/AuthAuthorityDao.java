package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.AuthAuthorityEntity;

import java.util.List;

public interface AuthAuthorityDao {

    List<AuthAuthorityEntity> create(List<AuthAuthorityEntity> authorities);
}
