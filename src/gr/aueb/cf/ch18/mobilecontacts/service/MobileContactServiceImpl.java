package gr.aueb.cf.ch18.mobilecontacts.service;

import gr.aueb.cf.ch18.mobilecontacts.dao.IMobileContactDAO;
import gr.aueb.cf.ch18.mobilecontacts.dto.MobileContactDTO;
import gr.aueb.cf.ch18.mobilecontacts.dto.UserDetailsDTO;
import gr.aueb.cf.ch18.mobilecontacts.model.MobileContact;
import gr.aueb.cf.ch18.mobilecontacts.model.UserDetails;
import gr.aueb.cf.ch18.mobilecontacts.service.exceptions.ContactNotFoundException;
import gr.aueb.cf.ch18.mobilecontacts.service.exceptions.PhoneNumberAlreadyExistsException;
import gr.aueb.cf.ch18.mobilecontacts.service.exceptions.UserIdAlreadyExistsException;

import java.util.List;

public class MobileContactServiceImpl implements IMobileContactService{

    /**
     * the Service Layer Class that implements the Decorator Pattern,
     * Delegation Pattern, Proxy Pattern (Wrapper Class of DAO)
     */
    private final IMobileContactDAO dao;
    public MobileContactServiceImpl(IMobileContactDAO dao) {
        this.dao = dao;
    }

    @Override
    public MobileContact insertMobileContact(MobileContactDTO mobileContactDTO) throws PhoneNumberAlreadyExistsException, UserIdAlreadyExistsException {
        MobileContact mobileContact;
        try {
            mobileContact = new MobileContact();
            mapMobileContact(mobileContact, mobileContactDTO);

            if (dao.phoneNumberExists(mobileContact.getPhoneNumber())) {
                throw new PhoneNumberAlreadyExistsException(mobileContact);
            }
            if (dao.userIdExists(mobileContact.getId())) {
                throw new UserIdAlreadyExistsException(mobileContact);
            }
            return dao.insert(mobileContact);

        } catch (PhoneNumberAlreadyExistsException | UserIdAlreadyExistsException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public MobileContact updateMobileContact(MobileContactDTO mobileContactDTO) throws PhoneNumberAlreadyExistsException, ContactNotFoundException {
        MobileContact mobileContact;

        try {
            mobileContact = new MobileContact();
            mapMobileContact(mobileContact, mobileContactDTO);

            if (dao.phoneNumberExists(mobileContact.getPhoneNumber())) {
                throw new PhoneNumberAlreadyExistsException(mobileContact);
            }
            if (!dao.userIdExists(mobileContact.getId())) {
                throw new ContactNotFoundException(mobileContact);
            }


            return dao.update(mobileContact);

        } catch (PhoneNumberAlreadyExistsException | ContactNotFoundException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void deleteContact(String phoneNumber) throws ContactNotFoundException {
       // MobileContact mobileContact;
        try {
           // mobileContact = new MobileContact();

            if (!dao.phoneNumberExists(phoneNumber)) {
                throw new ContactNotFoundException(phoneNumber);

            }
            dao.delete(phoneNumber);
        } catch (ContactNotFoundException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public MobileContact getContact(String phoneNumber) throws ContactNotFoundException {
        MobileContact mobileContact;
        try {
            mobileContact = dao.get(phoneNumber);
            if (mobileContact == null) {
                throw new ContactNotFoundException(phoneNumber);
            }
            return mobileContact;
        } catch (ContactNotFoundException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public MobileContact getContact(Long id) throws ContactNotFoundException {
        MobileContact mobileContact;
        try {
            mobileContact = dao.get(id);
            if (mobileContact == null) {
                throw new ContactNotFoundException(id);
            }
            return mobileContact;
        } catch (ContactNotFoundException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<MobileContact> getAllContacts() {
        return dao.getAll();
    }

    /**
     * Maps {@link MobileContactDTO} to {@link MobileContact}
     *
     * @param mobileContact
     *          the {@link MobileContact} under creation
     * @param mobileContactDTO
     *          the Mobile Contact transfer object
     */
    private void mapMobileContact(MobileContact mobileContact, MobileContactDTO mobileContactDTO) {
        mobileContact.setId(mobileContactDTO.getMobileContactId());
        mobileContact.setPhoneNumber(mobileContactDTO.getPhoneNumber());
        UserDetails userDetails = new UserDetails();
        //map user details
        mapUserDetails(userDetails, mobileContactDTO.getUserDetailsDTO());
        //set details
        mobileContact.setUserDetails(userDetails);

    }

    /**
     * Maps {@link UserDetailsDTO} to {@link UserDetails}
     *
     * @param userDetails
     *          the {@link UserDetails} under creation
     * @param userDetailsDTO
     *          the UserDetails Transfer object
     */
    private void mapUserDetails(UserDetails userDetails, UserDetailsDTO userDetailsDTO) {
        userDetails.setId(userDetailsDTO.getUserDetailsId());
        userDetails.setFirstname(userDetailsDTO.getFirstname());
        userDetails.setLastname(userDetailsDTO.getLastname());
    }
}
