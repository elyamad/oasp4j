package io.oasp.gastronomy.restaurant.doctorsmanagement.logic.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.oasp.gastronomy.restaurant.doctorsmanagement.dataaccess.api.DoctorEntity;
import io.oasp.gastronomy.restaurant.doctorsmanagement.dataaccess.api.dao.DoctorDao;
import io.oasp.gastronomy.restaurant.doctorsmanagement.logic.api.Doctorsmanagement;
import io.oasp.gastronomy.restaurant.doctorsmanagement.logic.api.to.DoctorEto;
import io.oasp.gastronomy.restaurant.general.common.api.constants.PermissionConstants;
import io.oasp.gastronomy.restaurant.general.logic.base.AbstractComponentFacade;

/**
 * Implementation class for {@link Doctorsmanagement}.
 *
 */
@Named
@Transactional
@Component
public class DoctorsmanagementImpl extends AbstractComponentFacade implements Doctorsmanagement {

  private static final Logger LOG = LoggerFactory.getLogger(DoctorsmanagementImpl.class);

  private DoctorDao doctorDao;

  @Override
  @RolesAllowed(PermissionConstants.FIND_DOCTOR)
  public DoctorEto findDoctor(long id) {

    LOG.debug("Get Doctor with id '" + id + "' from database.");

    return getBeanMapper().map(getDoctorDao().findOne(id), DoctorEto.class);

  }

  @Override
  @RolesAllowed(PermissionConstants.FIND_DOCTOR)
  public List<DoctorEto> findAllDoctor() {

    List<DoctorEntity> doctors = getDoctorDao().findAll();
    List<DoctorEto> doctorsBo = new ArrayList<>();

    for (DoctorEntity doctor : doctors) {
      doctorsBo.add(getBeanMapper().map(doctor, DoctorEto.class));
    }

    return doctorsBo;
  }

  @Override
  @RolesAllowed(PermissionConstants.SAVE_DOCTOR)
  public DoctorEto saveDoctor(DoctorEto doctor) {

    Objects.requireNonNull(doctor, "doctor");

    Long id = doctor.getId();
    DoctorEntity targetDoctor = null;

    if (id != null) {
      targetDoctor = getDoctorDao().findOne(id);
    }
    if (targetDoctor == null) {
      // StaffMember is new: -> create
      LOG.debug("Saving Doctor with id '{}' to the database.", id);
    } else {
      // Doctor already exists: -> Update
      LOG.debug("Updating Doctor with id '{}' in the database.", id);
      if (!Objects.equals(targetDoctor.getNom(), doctor.getNom())) {
        LOG.debug("Changing login of StaffMember with id '{}' from '{}' to '{}' in the database.", id,
            targetDoctor.getNom(), doctor.getNom());
      }
    }
    DoctorEntity persistedDoctor = getDoctorDao().save(getBeanMapper().map(doctor, DoctorEntity.class));
    return getBeanMapper().map(persistedDoctor, DoctorEto.class);
  }

  /**
   * @return the {@link DoctorDao} instance.
   */
  public DoctorDao getDoctorDao() {

    return this.doctorDao;
  }

  @Override
  @RolesAllowed(PermissionConstants.DELETE_DOCTOR)
  public void deleteDoctor(long doctorId) {

    getDoctorDao().delete(doctorId);
  }

  /**
   * Sets the field 'doctorDao'.
   *
   * @param doctorDao New value for doctorDao
   */

  @Inject
  public void setDoctorDao(DoctorDao doctorDao) {

    this.doctorDao = doctorDao;
  }

  /**
   * The constructor.
   */
  public DoctorsmanagementImpl() {

    super();
  }
}
