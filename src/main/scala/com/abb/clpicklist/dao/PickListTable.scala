package com.abb.clpicklist.dao

import java.time.{ZonedDateTime}

import scalikejdbc.{DB => SDB, _}
import scalikejdbc.jsr310._  // Java 8 Date Time API

import com.abb.clpicklist.util.Config


case class PickListTable(
  listId: BigDecimal,
  name: String,
  alternateName: String,
  inCategory: BigDecimal,
  descriptionCodex: Option[BigDecimal] = None,
  minLevel: BigDecimal,
  maxLevel: BigDecimal,
  isDaDk: BigDecimal,
  isMaDk: BigDecimal,
  isMaCe: BigDecimal,
  isMaJ2me: BigDecimal,
  coreDescription: Option[String] = None,
  sqlCode: Option[String] = None,
  contentsXml: Option[String] = None,
  updateCount: BigDecimal,
  updatedBy: String,
  lockedBySession: Option[String] = None,
  locked: Option[ZonedDateTime] = None,
  codeLength: Option[BigDecimal] = None,
  descriptionLength: Option[BigDecimal] = None,
  aux1Length: Option[BigDecimal] = None,
  aux2Length: Option[BigDecimal] = None,
  aux3Length: Option[BigDecimal] = None,
  aux4Length: Option[BigDecimal] = None,
  aux5Length: Option[BigDecimal] = None,
  filterKeyLength: Option[BigDecimal] = None) {

  def save()(implicit session: DBSession): PickListTable = PickListTable.save(this)(session)

  def destroy()(implicit session: DBSession): Unit = PickListTable.destroy(this)(session)

}


object PickListTable extends SQLSyntaxSupport[PickListTable] {

  // Used for pick_list_seq
  @volatile private var sequence: Long = 69
  private val SEQUENCE_START = 2600000

  override val tableName = "PICK_LIST"

  override val columns = Seq("LIST_ID", "NAME", "ALTERNATE_NAME", "IN_CATEGORY", "DESCRIPTION_CODEX", "MIN_LEVEL", "MAX_LEVEL", "IS_DA_DK", "IS_MA_DK", "IS_MA_CE", "IS_MA_J2ME", "CORE_DESCRIPTION", "SQL_CODE", "CONTENTS_XML", "UPDATE_COUNT", "UPDATED_BY", "LOCKED_BY_SESSION", "LOCKED", "CODE_LENGTH", "DESCRIPTION_LENGTH", "AUX1_LENGTH", "AUX2_LENGTH", "AUX3_LENGTH", "AUX4_LENGTH", "AUX5_LENGTH", "FILTER_KEY_LENGTH")

  def apply(pl: SyntaxProvider[PickListTable])(rs: WrappedResultSet): PickListTable = apply(pl.resultName)(rs)
  def apply(pl: ResultName[PickListTable])(rs: WrappedResultSet): PickListTable = new PickListTable(
    listId = rs.get(pl.listId),
    name = rs.get(pl.name),
    alternateName = rs.get(pl.alternateName),
    inCategory = rs.get(pl.inCategory),
    descriptionCodex = rs.get(pl.descriptionCodex),
    minLevel = rs.get(pl.minLevel),
    maxLevel = rs.get(pl.maxLevel),
    isDaDk = rs.get(pl.isDaDk),
    isMaDk = rs.get(pl.isMaDk),
    isMaCe = rs.get(pl.isMaCe),
    isMaJ2me = rs.get(pl.isMaJ2me),
    coreDescription = rs.get(pl.coreDescription),
    sqlCode = rs.get(pl.sqlCode),
    contentsXml = rs.get(pl.contentsXml),
    updateCount = rs.get(pl.updateCount),
    updatedBy = rs.get(pl.updatedBy),
    lockedBySession = rs.get(pl.lockedBySession),
    locked = rs.get(pl.locked),
    codeLength = rs.get(pl.codeLength),
    descriptionLength = rs.get(pl.descriptionLength),
    aux1Length = rs.get(pl.aux1Length),
    aux2Length = rs.get(pl.aux2Length),
    aux3Length = rs.get(pl.aux3Length),
    aux4Length = rs.get(pl.aux4Length),
    aux5Length = rs.get(pl.aux5Length),
    filterKeyLength = rs.get(pl.filterKeyLength)
  )

  val pl = PickListTable.syntax("pl")

  override val autoSession = AutoSession

  def find(listId: BigDecimal)(implicit session: DBSession): Option[PickListTable] = {
    withSQL {
      select.from(PickListTable as pl).where.eq(pl.listId, listId)
    }.map(PickListTable(pl.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession): List[PickListTable] = {
    withSQL(select.from(PickListTable as pl)).map(PickListTable(pl.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession): Long = {
    withSQL(select(sqls.count).from(PickListTable as pl)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession): Option[PickListTable] = {
    withSQL {
      select.from(PickListTable as pl).where.append(where)
    }.map(PickListTable(pl.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession): List[PickListTable] = {
    withSQL {
      select.from(PickListTable as pl).where.append(where)
    }.map(PickListTable(pl.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession): Long = {
    withSQL {
      select(sqls.count).from(PickListTable as pl).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    listId: BigDecimal,
    name: String,
    alternateName: String,
    inCategory: BigDecimal,
    descriptionCodex: Option[BigDecimal] = None,
    minLevel: BigDecimal,
    maxLevel: BigDecimal,
    isDaDk: BigDecimal,
    isMaDk: BigDecimal,
    isMaCe: BigDecimal,
    isMaJ2me: BigDecimal,
    coreDescription: Option[String] = None,
    sqlCode: Option[String] = None,
    contentsXml: Option[String] = None,
    updateCount: BigDecimal,
    updatedBy: String,
    lockedBySession: Option[String] = None,
    locked: Option[ZonedDateTime] = None,
    codeLength: Option[BigDecimal] = None,
    descriptionLength: Option[BigDecimal] = None,
    aux1Length: Option[BigDecimal] = None,
    aux2Length: Option[BigDecimal] = None,
    aux3Length: Option[BigDecimal] = None,
    aux4Length: Option[BigDecimal] = None,
    aux5Length: Option[BigDecimal] = None,
    filterKeyLength: Option[BigDecimal] = None)(implicit session: DBSession): PickListTable = {
    withSQL {
      insert.into(PickListTable).columns(
        column.listId,
        column.name,
        column.alternateName,
        column.inCategory,
        column.descriptionCodex,
        column.minLevel,
        column.maxLevel,
        column.isDaDk,
        column.isMaDk,
        column.isMaCe,
        column.isMaJ2me,
        column.coreDescription,
        column.sqlCode,
        column.contentsXml,
        column.updateCount,
        column.updatedBy,
        column.lockedBySession,
        column.locked,
        column.codeLength,
        column.descriptionLength,
        column.aux1Length,
        column.aux2Length,
        column.aux3Length,
        column.aux4Length,
        column.aux5Length,
        column.filterKeyLength
      ).values(
        listId,
        name,
        alternateName,
        inCategory,
        descriptionCodex,
        minLevel,
        maxLevel,
        isDaDk,
        isMaDk,
        isMaCe,
        isMaJ2me,
        coreDescription,
        sqlCode,
        contentsXml,
        updateCount,
        updatedBy,
        lockedBySession,
        locked,
        codeLength,
        descriptionLength,
        aux1Length,
        aux2Length,
        aux3Length,
        aux4Length,
        aux5Length,
        filterKeyLength
      )
    }.update.apply()

    new PickListTable(
      listId = listId,
      name = name,
      alternateName = alternateName,
      inCategory = inCategory,
      descriptionCodex = descriptionCodex,
      minLevel = minLevel,
      maxLevel = maxLevel,
      isDaDk = isDaDk,
      isMaDk = isMaDk,
      isMaCe = isMaCe,
      isMaJ2me = isMaJ2me,
      coreDescription = coreDescription,
      sqlCode = sqlCode,
      contentsXml = contentsXml,
      updateCount = updateCount,
      updatedBy = updatedBy,
      lockedBySession = lockedBySession,
      locked = locked,
      codeLength = codeLength,
      descriptionLength = descriptionLength,
      aux1Length = aux1Length,
      aux2Length = aux2Length,
      aux3Length = aux3Length,
      aux4Length = aux4Length,
      aux5Length = aux5Length,
      filterKeyLength = filterKeyLength)
  }

  def batchInsert(entities: Seq[PickListTable])(implicit session: DBSession): Seq[Int] = {
    val params: Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'listId -> entity.listId,
        'name -> entity.name,
        'alternateName -> entity.alternateName,
        'inCategory -> entity.inCategory,
        'descriptionCodex -> entity.descriptionCodex,
        'minLevel -> entity.minLevel,
        'maxLevel -> entity.maxLevel,
        'isDaDk -> entity.isDaDk,
        'isMaDk -> entity.isMaDk,
        'isMaCe -> entity.isMaCe,
        'isMaJ2me -> entity.isMaJ2me,
        'coreDescription -> entity.coreDescription,
        'sqlCode -> entity.sqlCode,
        'contentsXml -> entity.contentsXml,
        'updateCount -> entity.updateCount,
        'updatedBy -> entity.updatedBy,
        'lockedBySession -> entity.lockedBySession,
        'locked -> entity.locked,
        'codeLength -> entity.codeLength,
        'descriptionLength -> entity.descriptionLength,
        'aux1Length -> entity.aux1Length,
        'aux2Length -> entity.aux2Length,
        'aux3Length -> entity.aux3Length,
        'aux4Length -> entity.aux4Length,
        'aux5Length -> entity.aux5Length,
        'filterKeyLength -> entity.filterKeyLength))
        SQL("""INSERT INTO pick_list(
        list_id,
        name,
        alternate_name,
        in_category,
        description_codex,
        min_level,
        max_level,
        is_da_dk,
        is_ma_dk,
        is_ma_ce,
        is_ma_j2me,
        core_description,
        sql_code,
        contents_xml,
        update_count,
        updated_by,
        locked_by_session,
        locked,
        code_length,
        description_length,
        aux1_length,
        aux2_length,
        aux3_length,
        aux4_length,
        aux5_length,
        filter_key_length
      ) VALUES (
        {listId},
        {name},
        {alternateName},
        {inCategory},
        {descriptionCodex},
        {minLevel},
        {maxLevel},
        {isDaDk},
        {isMaDk},
        {isMaCe},
        {isMaJ2me},
        {coreDescription},
        {sqlCode},
        {contentsXml},
        {updateCount},
        {updatedBy},
        {lockedBySession},
        {locked},
        {codeLength},
        {descriptionLength},
        {aux1Length},
        {aux2Length},
        {aux3Length},
        {aux4Length},
        {aux5Length},
        {filterKeyLength}
      )""").batchByName(params: _*).apply()
    }

  def save(entity: PickListTable)(implicit session: DBSession): PickListTable = {
    withSQL {
      update(PickListTable).set(
        column.listId -> entity.listId,
        column.name -> entity.name,
        column.alternateName -> entity.alternateName,
        column.inCategory -> entity.inCategory,
        column.descriptionCodex -> entity.descriptionCodex,
        column.minLevel -> entity.minLevel,
        column.maxLevel -> entity.maxLevel,
        column.isDaDk -> entity.isDaDk,
        column.isMaDk -> entity.isMaDk,
        column.isMaCe -> entity.isMaCe,
        column.isMaJ2me -> entity.isMaJ2me,
        column.coreDescription -> entity.coreDescription,
        column.sqlCode -> entity.sqlCode,
        column.contentsXml -> entity.contentsXml,
        column.updateCount -> entity.updateCount,
        column.updatedBy -> entity.updatedBy,
        column.lockedBySession -> entity.lockedBySession,
        column.locked -> entity.locked,
        column.codeLength -> entity.codeLength,
        column.descriptionLength -> entity.descriptionLength,
        column.aux1Length -> entity.aux1Length,
        column.aux2Length -> entity.aux2Length,
        column.aux3Length -> entity.aux3Length,
        column.aux4Length -> entity.aux4Length,
        column.aux5Length -> entity.aux5Length,
        column.filterKeyLength -> entity.filterKeyLength
      ).where.eq(column.listId, entity.listId)
    }.update.apply()
    entity
  }

  def destroy(entity: PickListTable)(implicit session: DBSession): Unit = {
    withSQL { delete.from(PickListTable).where.eq(column.listId, entity.listId) }.update.apply()
  }

  def nextStaticSequence(): Long = sequence

  def nextSequenceVal: Option[java.math.BigDecimal] = {
    NamedDB(Config.odbConf.name) localTx { implicit session =>
      sql"SELECT pick_entry_key_seq.NEXTVAL FROM dual"
        .map(rs => rs.bigDecimal(1)) // extracts values from rich java.sql.ResultSet
        .single                      // single, list, traversable
        .apply()                     // Side effect!!! runs the SQL using Connection
    }
  }
}
