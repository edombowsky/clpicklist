package com.abb.clpicklist.dao

import java.time.{ZonedDateTime}

import scalikejdbc._, jsr310._


case class PickCategoryTable(
  categoryId: BigDecimal,
  name: String,
  alternateName: String,
  listClass: BigDecimal,
  descriptionCodex: Option[BigDecimal] = None,
  coreDescription: Option[String] = None) {

  def save()(implicit session: DBSession): PickCategoryTable = PickCategoryTable.save(this)(session)

  def destroy()(implicit session: DBSession): Unit = PickCategoryTable.destroy(this)(session)

}


object PickCategoryTable extends SQLSyntaxSupport[PickCategoryTable] {

  override val tableName = "PICK_CATEGORY"

  override val columns = Seq("CATEGORY_ID", "NAME", "ALTERNATE_NAME", "LIST_CLASS", "DESCRIPTION_CODEX", "CORE_DESCRIPTION")

  def apply(pc: SyntaxProvider[PickCategoryTable])(rs: WrappedResultSet): PickCategoryTable = apply(pc.resultName)(rs)
  def apply(pc: ResultName[PickCategoryTable])(rs: WrappedResultSet): PickCategoryTable = new PickCategoryTable(
    categoryId = rs.get(pc.categoryId),
    name = rs.get(pc.name),
    alternateName = rs.get(pc.alternateName),
    listClass = rs.get(pc.listClass),
    descriptionCodex = rs.get(pc.descriptionCodex),
    coreDescription = rs.get(pc.coreDescription)
  )

  val pc = PickCategoryTable.syntax("pc")

  override val autoSession = AutoSession

  def find(categoryId: BigDecimal)(implicit session: DBSession): Option[PickCategoryTable] = {
    withSQL {
      select.from(PickCategoryTable as pc).where.eq(pc.categoryId, categoryId)
    }.map(PickCategoryTable(pc.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession): List[PickCategoryTable] = {
    withSQL(select.from(PickCategoryTable as pc)).map(PickCategoryTable(pc.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession): Long = {
    withSQL(select(sqls.count).from(PickCategoryTable as pc)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession): Option[PickCategoryTable] = {
    withSQL {
      select.from(PickCategoryTable as pc).where.append(where)
    }.map(PickCategoryTable(pc.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession): List[PickCategoryTable] = {
    withSQL {
      select.from(PickCategoryTable as pc).where.append(where)
    }.map(PickCategoryTable(pc.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession): Long = {
    withSQL {
      select(sqls.count).from(PickCategoryTable as pc).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    categoryId: BigDecimal,
    name: String,
    alternateName: String,
    listClass: BigDecimal,
    descriptionCodex: Option[BigDecimal] = None,
    coreDescription: Option[String] = None)(implicit session: DBSession): PickCategoryTable = {
    withSQL {
      insert.into(PickCategoryTable).columns(
        column.categoryId,
        column.name,
        column.alternateName,
        column.listClass,
        column.descriptionCodex,
        column.coreDescription
      ).values(
        categoryId,
        name,
        alternateName,
        listClass,
        descriptionCodex,
        coreDescription
      )
    }.update.apply()

    PickCategoryTable(
      categoryId = categoryId,
      name = name,
      alternateName = alternateName,
      listClass = listClass,
      descriptionCodex = descriptionCodex,
      coreDescription = coreDescription)
  }

  def batchInsert(entities: Seq[PickCategoryTable])(implicit session: DBSession): Seq[Int] = {
    val params: Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'categoryId -> entity.categoryId,
        'name -> entity.name,
        'alternateName -> entity.alternateName,
        'listClass -> entity.listClass,
        'descriptionCodex -> entity.descriptionCodex,
        'coreDescription -> entity.coreDescription))
        SQL("""INSERT INTO pick_category(
        category_id,
        name,
        alternate_name,
        list_class,
        description_codex,
        core_description
      ) VALUES (
        {categoryId},
        {name},
        {alternateName},
        {listClass},
        {descriptionCodex},
        {coreDescription}
      )""").batchByName(params: _*).apply()
    }

  def save(entity: PickCategoryTable)(implicit session: DBSession): PickCategoryTable = {
    withSQL {
      update(PickCategoryTable).set(
        column.categoryId -> entity.categoryId,
        column.name -> entity.name,
        column.alternateName -> entity.alternateName,
        column.listClass -> entity.listClass,
        column.descriptionCodex -> entity.descriptionCodex,
        column.coreDescription -> entity.coreDescription
      ).where.eq(column.categoryId, entity.categoryId)
    }.update.apply()
    entity
  }

  def destroy(entity: PickCategoryTable)(implicit session: DBSession): Unit = {
    withSQL { delete.from(PickCategoryTable).where.eq(column.categoryId, entity.categoryId) }.update.apply()
  }

}
