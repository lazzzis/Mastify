package com.github.whitescent.mastify.database.dao

import androidx.room.*
import com.github.whitescent.mastify.database.model.AccountEntity

@Dao
interface AccountDao {

  @Upsert(entity = AccountEntity::class)
  fun insertOrReplace(account: AccountEntity)

  @Delete
  fun delete(account: AccountEntity)

  @Query("SELECT * FROM AccountEntity ORDER BY id ASC")
  fun loadAll(): List<AccountEntity>
}