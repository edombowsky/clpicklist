package com.abb.clpicklist.dao

import java.time.{ZonedDateTime}

import scalikejdbc._, jsr310._


case class PickBranchTable(
  branchId: BigDecimal,
  ofList: BigDecimal,
  branchNum: BigDecimal,
  isLatest: BigDecimal,
  created: ZonedDateTime) {

  def save()(implicit session: DBSession): PickBranchTable = PickBranchTable.save(this)(session)

  def destroy()(implicit session: DBSession): Unit = PickBranchTable.destroy(this)(session)

}


object PickBranchTable extends SQLSyntaxSupport[PickBranchTable] {

  override val tableName = "PICK_BRANCH"

  override val columns = Seq("BRANCH_ID", "OF_LIST", "BRANCH_NUM", "IS_LATEST", "CREATED")

  def apply(pb: SyntaxProvider[PickBranchTable])(rs: WrappedResultSet): PickBranchTable = apply(pb.resultName)(rs)
  def apply(pb: ResultName[PickBranchTable])(rs: WrappedResultSet): PickBranchTable = new PickBranchTable(
    // rs.BigDecimal(pb.branchId),
    // rs.BigDecimal(pb.ofList),
    // rs.BigDecimal(pb.branchNum),
    // rs.BigDecimal(pb.isLatest),
    // rs.BigDecimal(pb.created)
    branchId = rs.bigDecimal(pb.branchId),
    ofList = rs.bigDecimal(pb.ofList),
    branchNum = rs.bigDecimal(pb.branchNum),
    isLatest = rs.bigDecimal(pb.isLatest),
    created = rs.zonedDateTime(pb.created)
  )

  val pb = PickBranchTable.syntax("pb")

  override val autoSession = AutoSession

  def find(branchId: BigDecimal)(implicit session: DBSession): Option[PickBranchTable] = {
    withSQL {
      select.from(PickBranchTable as pb).where.eq(pb.branchId, branchId)
    }.map(PickBranchTable(pb.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession): List[PickBranchTable] = {
    withSQL(select.from(PickBranchTable as pb)).map(PickBranchTable(pb.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession): Long = {
    withSQL(select(sqls.count).from(PickBranchTable as pb)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession): Option[PickBranchTable] = {
    withSQL {
      select.from(PickBranchTable as pb).where.append(where)
    }.map(PickBranchTable(pb.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession): List[PickBranchTable] = {
    withSQL {
      select.from(PickBranchTable as pb).where.append(where)
    }.map(PickBranchTable(pb.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession): Long = {
    withSQL {
      select(sqls.count).from(PickBranchTable as pb).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    branchId: BigDecimal,
    ofList: BigDecimal,
    branchNum: BigDecimal,
    isLatest: BigDecimal,
    created: ZonedDateTime)(implicit session: DBSession): PickBranchTable = {
    withSQL {
      insert.into(PickBranchTable).columns(
        column.branchId,
        column.ofList,
        column.branchNum,
        column.isLatest,
        column.created
      ).values(
        branchId,
        ofList,
        branchNum,
        isLatest,
        created
      )
    }.update.apply()

    PickBranchTable(
      branchId = branchId,
      ofList = ofList,
      branchNum = branchNum,
      isLatest = isLatest,
      created = created)
  }

  def batchInsert(entities: Seq[PickBranchTable])(implicit session: DBSession): Seq[Int] = {
    val params: Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'branchId -> entity.branchId,
        'ofList -> entity.ofList,
        'branchNum -> entity.branchNum,
        'isLatest -> entity.isLatest,
        'created -> entity.created))
        SQL("""INSERT INTO pick_branch(
        branch_id,
        of_list,
        branch_num,
        is_latest,
        created
      ) values (
        {branchId},
        {ofList},
        {branchNum},
        {isLatest},
        {created}
      )""").batchByName(params: _*).apply()
    }

  def save(entity: PickBranchTable)(implicit session: DBSession): PickBranchTable = {
    withSQL {
      update(PickBranchTable).set(
        column.branchId -> entity.branchId,
        column.ofList -> entity.ofList,
        column.branchNum -> entity.branchNum,
        column.isLatest -> entity.isLatest,
        column.created -> entity.created
      ).where.eq(column.branchId, entity.branchId)
    }.update.apply()
    entity
  }

  def destroy(entity: PickBranchTable)(implicit session: DBSession): Unit = {
    withSQL { delete.from(PickBranchTable).where.eq(column.branchId, entity.branchId) }.update.apply()
  }
}
